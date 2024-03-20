package io.github.wulkanowy.sdk.scrapper.repository

import io.github.wulkanowy.sdk.scrapper.Scrapper
import io.github.wulkanowy.sdk.scrapper.exception.ScrapperException
import io.github.wulkanowy.sdk.scrapper.exception.StudentGraduateException
import io.github.wulkanowy.sdk.scrapper.getDecodedKey
import io.github.wulkanowy.sdk.scrapper.getNormalizedSymbol
import io.github.wulkanowy.sdk.scrapper.getScriptParam
import io.github.wulkanowy.sdk.scrapper.interceptor.handleErrors
import io.github.wulkanowy.sdk.scrapper.isCurrentLoginHasEduOne
import io.github.wulkanowy.sdk.scrapper.login.CertificateResponse
import io.github.wulkanowy.sdk.scrapper.login.InvalidSymbolException
import io.github.wulkanowy.sdk.scrapper.login.LoginHelper
import io.github.wulkanowy.sdk.scrapper.login.NotLoggedInException
import io.github.wulkanowy.sdk.scrapper.login.UrlGenerator
import io.github.wulkanowy.sdk.scrapper.register.AuthInfo
import io.github.wulkanowy.sdk.scrapper.register.Diary
import io.github.wulkanowy.sdk.scrapper.register.HomePageResponse
import io.github.wulkanowy.sdk.scrapper.register.PermissionUnit
import io.github.wulkanowy.sdk.scrapper.register.Permissions
import io.github.wulkanowy.sdk.scrapper.register.RegisterEmployee
import io.github.wulkanowy.sdk.scrapper.register.RegisterSymbol
import io.github.wulkanowy.sdk.scrapper.register.RegisterUnit
import io.github.wulkanowy.sdk.scrapper.register.RegisterUser
import io.github.wulkanowy.sdk.scrapper.register.getStudentsFromDiaries
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository.Companion.SELECTOR_ADFS
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository.Companion.SELECTOR_ADFS_CARDS
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository.Companion.SELECTOR_ADFS_LIGHT
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository.Companion.SELECTOR_STANDARD
import io.github.wulkanowy.sdk.scrapper.service.RegisterService
import io.github.wulkanowy.sdk.scrapper.service.StudentPlusService
import io.github.wulkanowy.sdk.scrapper.service.StudentService
import io.github.wulkanowy.sdk.scrapper.service.SymbolService
import kotlinx.serialization.json.Json
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.parser.Parser
import org.jsoup.select.Elements
import org.slf4j.LoggerFactory
import pl.droidsonroids.jspoon.Jspoon
import java.nio.charset.StandardCharsets
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Suppress("UnnecessaryOptInAnnotation")
@OptIn(ExperimentalEncodingApi::class)
internal class RegisterRepository(
    private val startSymbol: String,
    private val email: String,
    private val password: String,
    private val loginHelper: LoginHelper,
    private val register: RegisterService,
    private val student: StudentService,
    private val studentPlus: StudentPlusService,
    private val symbolService: SymbolService,
    private val url: UrlGenerator,
) {

    private companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    private val certificateAdapter by lazy {
        Jspoon.create().adapter(CertificateResponse::class.java)
    }

    private val json = Json {
        ignoreUnknownKeys = true
    }

    suspend fun getUserSubjects(): RegisterUser {
        val symbolLoginType = getLoginType(startSymbol.getNormalizedSymbol())
        val certificateResponse = getCert(symbolLoginType)
        val (login, emailAddress, symbols) = certificateResponse.toCertificateValues()

        return RegisterUser(
            email = emailAddress.ifBlank { email },
            login = getNormalizedLogin(login, emailAddress).ifBlank { email },
            baseUrl = url.generate(UrlGenerator.Site.BASE),
            loginType = symbolLoginType,
            symbols = getRegisterSymbols(
                symbols = symbols,
                startLoginCert = certificateResponse,
                symbolLoginType = symbolLoginType,
            ),
        )
    }

    private suspend fun getRegisterSymbols(
        symbols: List<String>,
        startLoginCert: CertificateResponse,
        symbolLoginType: Scrapper.LoginType,
    ): List<RegisterSymbol> = symbols.map { symbol ->
        val homeResponse = runCatching {
            if (symbols.size > 5) {
                runCatching { symbolService.getSymbolPage(symbol) }
                    .onFailure { if (it is InvalidSymbolException) throw it }
            }

            url.symbol = symbol
            val loginCert = when (startSymbol) {
                symbol -> startLoginCert
                else -> getCert(symbolLoginType)
            }
            val res = loginHelper.sendCertificate(
                email = email,
                cert = loginCert,
                url = loginCert.action,
            )
            res
        }

        val errors = checkForErrors(symbolLoginType, homeResponse.getOrNull()?.document)

        val studentModuleUrls = homeResponse.getOrNull()?.studentSchools.orEmpty()
            .map { it.attr("href") }

        val userName = homeResponse.getOrNull().getUserNameFromUserData()
        val schools = homeResponse.getOrNull()
            .toPermissions()
            .toUnitsMap()
            .getRegisterUnits(userName, studentModuleUrls)

        loginHelper.logout()

        RegisterSymbol(
            symbol = symbol,
            error = homeResponse.exceptionOrNull() ?: errors,
            userName = userName,
            schools = schools,
        )
    }

    private fun checkForErrors(loginType: Scrapper.LoginType, document: Element?): Throwable? {
        document ?: return null

        val graduateMessage = document.selectFirst(".splashScreen")?.ownText().orEmpty()
        val invalidSymbolMessage = document.selectFirst("#page-error .error__box")?.text().orEmpty()

        val loginSelectors = when (loginType) {
            Scrapper.LoginType.STANDARD -> document.select(SELECTOR_STANDARD)
            Scrapper.LoginType.ADFS -> document.select(SELECTOR_ADFS)
            Scrapper.LoginType.ADFSLight, Scrapper.LoginType.ADFSLightCufs, Scrapper.LoginType.ADFSLightScoped -> document.select(SELECTOR_ADFS_LIGHT)
            Scrapper.LoginType.ADFSCards -> document.select(SELECTOR_ADFS_CARDS)
            else -> Elements()
        }

        return when {
            "Twoje konto jest nieaktywne" in graduateMessage -> StudentGraduateException(graduateMessage)
            "musi mieć następujący format" in invalidSymbolMessage -> InvalidSymbolException(invalidSymbolMessage)
            loginSelectors.isNotEmpty() -> NotLoggedInException("Nie udało się zalogować")
            else -> null
        }
    }

    private suspend fun Map<PermissionUnit, AuthInfo?>.getRegisterUnits(userName: String, studentModuleUrls: List<String>): List<RegisterUnit> {
        return map { (unit, authInfo) ->
            url.schoolId = unit.symbol

            val isEduOne = isCurrentLoginHasEduOne(studentModuleUrls, url)
            val cacheAndDiaries = runCatching {
                if (authInfo?.parentIds.isNullOrEmpty() && authInfo?.studentIds.isNullOrEmpty()) {
                    null to emptyList()
                } else {
                    val eduOneDiaries = getEduOneDiaries()
                    when {
                        isEduOne -> eduOneDiaries.any { it.first } to eduOneDiaries.map { it.second }
                        else -> isStudentFromParentAccount() to getStudentDiaries()
                    }
                }
            }

            val employees = authInfo?.employeeIds?.map { employeeId ->
                RegisterEmployee(
                    employeeId = employeeId,
                    employeeName = userName,
                )
            }
            val students = cacheAndDiaries.getOrNull()?.let { (cache, diaries) ->
                getStudentsFromDiaries(
                    diaries = diaries,
                    isParent = cache,
                    isEduOne = isEduOne,
                    unitId = unit.id,
                )
            }

            RegisterUnit(
                userLoginId = requireNotNull(authInfo?.loginId),
                schoolId = unit.symbol,
                schoolName = unit.name,
                schoolShortName = unit.short,
                error = cacheAndDiaries.exceptionOrNull(),
                employeeIds = authInfo?.employeeIds.orEmpty(),
                studentIds = authInfo?.studentIds.orEmpty(),
                parentIds = authInfo?.parentIds.orEmpty(),
                subjects = employees.orEmpty() + students.orEmpty(),
            )
        }
    }

    private suspend fun getLoginType(symbol: String): Scrapper.LoginType {
        runCatching { symbolService.getSymbolPage(symbol) }
            .onFailure { if (it is InvalidSymbolException) throw it }

        val urlGenerator = url.also { it.symbol = symbol }
        val page = register.getFormType(urlGenerator.generate(UrlGenerator.Site.LOGIN) + "Account/LogOn").page
        return when {
            page.select(SELECTOR_STANDARD).isNotEmpty() -> Scrapper.LoginType.STANDARD
            page.select(SELECTOR_ADFS).isNotEmpty() -> Scrapper.LoginType.ADFS
            page.select(SELECTOR_ADFS_LIGHT).isNotEmpty() -> {
                page.selectFirst("form")?.attr("action").orEmpty().run {
                    when {
                        contains("cufs.edu.gdansk.pl") -> Scrapper.LoginType.ADFS // for compatibility with old accounts
                        contains("cufs.edu.lublin.eu") -> Scrapper.LoginType.ADFSLightCufs
                        startsWith("/LoginPage.aspx") -> Scrapper.LoginType.ADFSLight
                        startsWith("/${urlGenerator.symbol}/LoginPage.aspx") -> Scrapper.LoginType.ADFSLightScoped
                        else -> throw ScrapperException("Nieznany typ dziennika ADFS: ${page.text()}")
                    }
                }
            }

            page.select(SELECTOR_ADFS_CARDS).isNotEmpty() -> Scrapper.LoginType.ADFSCards
            else -> throw ScrapperException("Nieznany typ dziennika: ${page.text()}")
        }
    }

    private fun getNormalizedLogin(login: String, email: String): String = when (email) {
        login.lowercase() -> email // AttributeName="name" contains entered email in standard login
        else -> login
    }

    private suspend fun getCert(symbolLoginType: Scrapper.LoginType): CertificateResponse {
        logger.debug("Register login type: {}", symbolLoginType)
        return loginHelper
            .apply { loginType = symbolLoginType }
            .sendCredentials(email, password)
    }

    private fun CertificateResponse.toCertificateValues(): Triple<String, String, List<String>> {
        val cert = Jsoup.parse(wresult.replace(":", ""), "", Parser.xmlParser())

        val symbols = cert.select("[AttributeName$=\"Instance\"] samlAttributeValue")
        val emailAddress = cert.select("[AttributeName=\"emailaddress\"] samlAttributeValue").text()
        val login = cert.select("[AttributeName=\"name\"] samlAttributeValue").text()

        return Triple(login, emailAddress, symbols.toNormalizedSymbols())
    }

    private fun Elements.toNormalizedSymbols(): List<String> = this
        .map { it.text().trim() }
        .apply { logger.debug("{}", this) }
        .filter { it.matches("[a-zA-Z0-9]*".toRegex()) } // early filter invalid symbols
        .filter { it != "Default" }
        .distinct()

    private fun HomePageResponse?.getUserNameFromUserData(): String {
        val data = this?.userData ?: return ""

        val adfsName = data.substringBefore(" (", "")
        val standardName = data.substringBefore(" - ", adfsName)

        return standardName.takeIf { it.isNotBlank() }.orEmpty()
    }

    private fun HomePageResponse?.toPermissions(): Permissions? {
        val base64 = getScriptParam("permissions", this?.document.toString()).substringBefore("|")
        return Base64.decode(base64).toString(StandardCharsets.UTF_8).takeIf { it.isNotBlank() }?.let {
            json.decodeFromString<Permissions>(it)
        }
    }

    private fun Permissions?.toUnitsMap(): Map<PermissionUnit, AuthInfo?> {
        return this?.units?.associateWith { unit ->
            authInfos.find { it.unitId == unit.id }
        }.orEmpty()
    }

    // used only for check is student from parent account
    private suspend fun isStudentFromParentAccount(): Boolean? {
        val studentPageUrl = url.generate(UrlGenerator.Site.STUDENT) + "LoginEndpoint.aspx"
        val start = student.getStart(studentPageUrl)

        val startPage = when {
            "Working" in Jsoup.parse(start).title() -> {
                val cert = certificateAdapter.fromHtml(start)
                student.sendModuleCertificate(
                    referer = url.createReferer(UrlGenerator.Site.STUDENT),
                    url = cert.action,
                    certificate = mapOf(
                        "wa" to cert.wa,
                        "wresult" to cert.wresult,
                        "wctx" to cert.wctx,
                    ),
                )
            }

            else -> start
        }

        val userCache = student.getUserCache(
            url = url.generate(UrlGenerator.Site.STUDENT) + "UczenCache.mvc/Get",
            token = getScriptParam("antiForgeryToken", startPage),
            appGuid = getScriptParam("appGuid", startPage),
            appVersion = getScriptParam("version", startPage),
        ).data

        return userCache?.isParent
    }

    private suspend fun getStudentDiaries(): List<Diary> = student
        .getSchoolInfo(url.generate(UrlGenerator.Site.STUDENT) + "UczenDziennik.mvc/Get")
        .handleErrors()
        .data.orEmpty()

    private suspend fun getEduOneDiaries(): List<Pair<Boolean, Diary>> {
        val baseStudentPlus = url.generate(UrlGenerator.Site.STUDENT_PLUS)
        val studentPageUrl = baseStudentPlus + "LoginEndpoint.aspx"
        val start = student.getStart(studentPageUrl)

        if ("Working" in Jsoup.parse(start).title()) {
            val cert = certificateAdapter.fromHtml(start)
            student.sendModuleCertificate(
                referer = url.createReferer(UrlGenerator.Site.STUDENT_PLUS),
                url = cert.action,
                certificate = mapOf(
                    "wa" to cert.wa,
                    "wresult" to cert.wresult,
                    "wctx" to cert.wctx,
                ),
            )
        }

        return studentPlus
            .getContext(url = baseStudentPlus + "api/Context").students
            .map { context ->
                val key = getDecodedKey(context.key)
                val semesters = kotlin.runCatching {
                    studentPlus.getSemesters(
                        url = baseStudentPlus + "api/OkresyKlasyfikacyjne",
                        key = context.key,
                        diaryId = context.registerId,
                    )
                }.onFailure {
                    logger.error("Can't fetch semesters", it)
                }.getOrNull().orEmpty().map { semester ->
                    Diary.Semester(
                        number = semester.numerOkresu,
                        start = semester.dataOd,
                        end = semester.dataDo,
                        unitId = key.unitId,
                        id = semester.id,
                        // todo
                        isLast = false,
                        level = 0,
                        classId = 0,
                    )
                }
                val level = context.className.takeWhile { it.isDigit() }
                context.opiekunUcznia to Diary(
                    id = context.registerId,
                    studentId = key.studentId,
                    studentName = context.studentName.substringBefore(" ", ""),
                    studentSecondName = context.studentName.substringAfter(" ", "").substringBefore(" ", ""),
                    studentSurname = context.studentName.substringAfterLast(" ", ""),
                    studentNick = "",
                    isDiary = true,
                    diaryId = key.diaryId,
                    kindergartenDiaryId = 0,
                    fosterDiaryId = 0,
                    level = level.toInt(), // todo
                    symbol = context.className.replace(level, ""), // todo
                    name = context.className,
                    year = context.registerDateFrom.year,
                    semesters = semesters,
                    start = context.registerDateFrom,
                    end = context.registerDateTo,
                    componentUnitId = key.unitId,
                    sioTypeId = null,
                    isAdults = context.isAdults,
                    isPostSecondary = context.isPolicealna,
                    is13 = context.is13,
                    isArtistic = context.isArtystyczna,
                    isArtistic13 = context.isArtystyczna13,
                    isSpecial = context.isSpecjalna,
                    isKindergarten = context.isPrzedszkolak,
                    isFoster = null,
                    isArchived = null,
                    isCharges = context.config.isPlatnosci,
                    isPayments = context.config.isOplaty,
                    isPayButtonOn = null,
                    canMergeAccounts = context.config.isScalanieKont,
                    fullName = context.studentName,
                    o365PassType = null,
                    isAdult = null,
                    isAuthorized = !context.isAuthorizationRequired,
                    citizenship = null,
                )
            }
    }
}

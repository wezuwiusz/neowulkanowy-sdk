package io.github.wulkanowy.sdk.scrapper.repository

import io.github.wulkanowy.sdk.scrapper.ApiEndpoints
import io.github.wulkanowy.sdk.scrapper.Scrapper
import io.github.wulkanowy.sdk.scrapper.exception.ScrapperException
import io.github.wulkanowy.sdk.scrapper.exception.StudentGraduateException
import io.github.wulkanowy.sdk.scrapper.getNormalizedSymbol
import io.github.wulkanowy.sdk.scrapper.getScriptParam
import io.github.wulkanowy.sdk.scrapper.interceptor.handleErrors
import io.github.wulkanowy.sdk.scrapper.isCurrentLoginHasEduOne
import io.github.wulkanowy.sdk.scrapper.login.CertificateResponse
import io.github.wulkanowy.sdk.scrapper.login.InvalidSymbolException
import io.github.wulkanowy.sdk.scrapper.login.LoginHelper
import io.github.wulkanowy.sdk.scrapper.login.NotLoggedInException
import io.github.wulkanowy.sdk.scrapper.login.UrlGenerator
import io.github.wulkanowy.sdk.scrapper.register.Diary
import io.github.wulkanowy.sdk.scrapper.register.HomePageResponse
import io.github.wulkanowy.sdk.scrapper.register.RegisterStudent
import io.github.wulkanowy.sdk.scrapper.register.RegisterSymbol
import io.github.wulkanowy.sdk.scrapper.register.RegisterUnit
import io.github.wulkanowy.sdk.scrapper.register.RegisterUser
import io.github.wulkanowy.sdk.scrapper.register.getStudentsFromDiaries
import io.github.wulkanowy.sdk.scrapper.register.mapToRegisterStudent
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository.Companion.SELECTOR_ADFS
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository.Companion.SELECTOR_ADFS_CARDS
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository.Companion.SELECTOR_ADFS_LIGHT
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository.Companion.SELECTOR_STANDARD
import io.github.wulkanowy.sdk.scrapper.service.RegisterService
import io.github.wulkanowy.sdk.scrapper.service.StudentPlusService
import io.github.wulkanowy.sdk.scrapper.service.StudentService
import io.github.wulkanowy.sdk.scrapper.service.SymbolService
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.parser.Parser
import org.jsoup.select.Elements
import org.slf4j.LoggerFactory
import pl.droidsonroids.jspoon.Jspoon
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

        val userName = homeResponse.getOrNull().getUserNameFromUserData()
        val schools = getRegisterUnits(homeResponse.getOrNull())

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
            "jedyne źródło pozyskania prawidłowego adresu" in invalidSymbolMessage -> InvalidSymbolException(invalidSymbolMessage)
            loginSelectors.isNotEmpty() -> NotLoggedInException("Nie udało się zalogować")
            else -> null
        }
    }

    private suspend fun getRegisterUnits(homeResponse: HomePageResponse?): List<RegisterUnit> {
        val studentModules = homeResponse?.studentSchools.orEmpty()
            .map { it.text() to it.attr("href") }

        return studentModules.flatMap { (name, url) ->
            getRegisterUnit(
                originalName = name,
                studentModuleUrl = url,
                studentModuleUrls = studentModules.map { it.second },
            )
        }
    }

    private suspend fun getRegisterUnit(
        originalName: String,
        studentModuleUrl: String,
        studentModuleUrls: List<String>,
    ): List<RegisterUnit> {
        val extractedSchoolId = studentModuleUrl.toHttpUrl().pathSegments[1]
        url.schoolId = extractedSchoolId
        val isEduOne = isCurrentLoginHasEduOne(studentModuleUrls, url)

        val originalSchoolShortName = when {
            isEduOne -> originalName.takeIf { it != "Uczeń Plus" }
            else -> originalName.takeIf { it != "Uczeń" }
        }

        val loginResult = runCatching {
            val site = when {
                isEduOne -> UrlGenerator.Site.STUDENT_PLUS
                else -> UrlGenerator.Site.STUDENT
            }
            loginModule(site)
        }

        val registerStudents = runCatching {
            when {
                isEduOne -> {
                    val (baseStudentPlus, _) = loginResult.getOrThrow()
                    getEduOneDiaries(baseStudentPlus)
                }

                else -> getStudentsFromOldModule(
                    loginResult = loginResult.getOrThrow(),
                    unitId = null, // will be extracted from diary
                )
            }
        }

        val students = registerStudents.getOrNull().orEmpty()

        return students.groupBy { it.unitId }.map { (_, students) ->
            val firstStudentFromUnit = students.firstOrNull()
            RegisterUnit(
                userLoginId = -1,
                schoolId = extractedSchoolId,
                schoolName = firstStudentFromUnit?.schoolName ?: "Nieznana pełna nazwa szkoły",
                schoolShortName = firstStudentFromUnit?.schoolNameShort ?: originalSchoolShortName.orEmpty(),
                error = registerStudents.exceptionOrNull(),
                employeeIds = emptyList(),
                studentIds = emptyList(),
                parentIds = emptyList(),
                subjects = students,
            )
        }.ifEmpty {
            listOf(
                RegisterUnit(
                    userLoginId = -1,
                    schoolId = extractedSchoolId,
                    schoolName = "Nieznana pełna nazwa szkoły",
                    schoolShortName = originalSchoolShortName.orEmpty(),
                    error = registerStudents.exceptionOrNull(),
                    employeeIds = emptyList(),
                    studentIds = emptyList(),
                    parentIds = emptyList(),
                    subjects = registerStudents.getOrDefault(emptyList()),
                ),
            )
        }
    }

    private suspend fun getStudentsFromOldModule(
        loginResult: Pair<String, String>,
        unitId: Int?,
    ): List<RegisterStudent> {
        val (_, startPage) = loginResult
        val isParent = isStudentFromParentAccount(startPage)
        val diaries = getStudentDiaries()
        return diaries.getStudentsFromDiaries(
            isParent = isParent,
            isEduOne = false,
            unitId = unitId
                ?: diaries.firstOrNull()?.componentUnitId
                ?: error("Can't find componentUnitId in student diaries"),
        ).map {
            it.copy(
                schoolName = getScriptParam("organizationName", startPage),
            )
        }
    }

    private suspend fun getStudentDiaries(): List<Diary> = student
        .getSchoolInfo(url.generate(UrlGenerator.Site.STUDENT) + "${ApiEndpoints.UczenDziennik}.mvc/Get")
        .handleErrors()
        .data.orEmpty()

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

    // used only for check is student from parent account
    private suspend fun isStudentFromParentAccount(startPage: String): Boolean? {
        val userCache = student.getUserCache(
            url = url.generate(UrlGenerator.Site.STUDENT) + "${ApiEndpoints.UczenCache}.mvc/Get",
            token = getScriptParam("antiForgeryToken", startPage),
            appGuid = getScriptParam("appGuid", startPage),
            appVersion = getScriptParam("version", startPage),
        ).data

        return userCache?.isParent
    }

    private suspend fun getEduOneDiaries(baseStudentPlus: String): List<RegisterStudent> {
        return studentPlus
            .getContextByUrl(url = baseStudentPlus + "api/${ApiEndpoints.PlusContext}").students
            .map { contextStudent ->
                val semesters = runCatching {
                    when {
                        contextStudent.isAuthorizationRequired -> emptyList()
                        else -> studentPlus.getSemestersByUrl(
                            url = baseStudentPlus + "api/${ApiEndpoints.PlusOkresyKlasyfikacyjne}",
                            key = contextStudent.key,
                            diaryId = contextStudent.registerId,
                        )
                    }
                }.onFailure {
                    logger.error("Can't fetch semesters", it)
                }.getOrNull().orEmpty()

                contextStudent.mapToRegisterStudent(semesters)
            }
    }

    private suspend fun loginModule(site: UrlGenerator.Site): Pair<String, String> {
        loginHelper.loginModule(site)

        val baseStudentPlus = url.generate(site)
        val studentPageUrl = baseStudentPlus + "LoginEndpoint.aspx"
        val start = student.getStart(studentPageUrl)

        val homepage = if ("Working" in Jsoup.parse(start).title()) {
            val cert = certificateAdapter.fromHtml(start)
            student.sendModuleCertificate(
                referer = url.createReferer(site),
                url = cert.action,
                certificate = mapOf(
                    "wa" to cert.wa,
                    "wresult" to cert.wresult,
                    "wctx" to cert.wctx,
                ),
            )
        } else {
            start
        }

        val appVersion = getScriptParam("version", homepage).ifBlank {
            getScriptParam("appVersion", homepage)
        }
        appVersion.substringAfterLast(".").toIntOrNull()?.let {
            ApiEndpoints.currentVersion = it
        }

        return baseStudentPlus to homepage
    }
}

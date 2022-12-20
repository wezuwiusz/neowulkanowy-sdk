package io.github.wulkanowy.sdk.scrapper.repository

import com.migcomponents.migbase64.Base64
import io.github.wulkanowy.sdk.scrapper.Scrapper
import io.github.wulkanowy.sdk.scrapper.exception.ScrapperException
import io.github.wulkanowy.sdk.scrapper.exception.TemporarilyDisabledException
import io.github.wulkanowy.sdk.scrapper.getNormalizedSymbol
import io.github.wulkanowy.sdk.scrapper.getScriptParam
import io.github.wulkanowy.sdk.scrapper.interceptor.handleErrors
import io.github.wulkanowy.sdk.scrapper.login.AccountPermissionException
import io.github.wulkanowy.sdk.scrapper.login.CertificateResponse
import io.github.wulkanowy.sdk.scrapper.login.LoginHelper
import io.github.wulkanowy.sdk.scrapper.register.AuthInfo
import io.github.wulkanowy.sdk.scrapper.register.Diary
import io.github.wulkanowy.sdk.scrapper.register.Permission
import io.github.wulkanowy.sdk.scrapper.register.Student
import io.github.wulkanowy.sdk.scrapper.register.Unit
import io.github.wulkanowy.sdk.scrapper.register.toSemesters
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository.Companion.SELECTOR_ADFS
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository.Companion.SELECTOR_ADFS_CARDS
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository.Companion.SELECTOR_ADFS_LIGHT
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository.Companion.SELECTOR_STANDARD
import io.github.wulkanowy.sdk.scrapper.service.RegisterService
import io.github.wulkanowy.sdk.scrapper.service.ServiceManager
import io.github.wulkanowy.sdk.scrapper.service.StudentService
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import org.slf4j.LoggerFactory
import java.nio.charset.StandardCharsets

class RegisterRepository(
    private val startSymbol: String,
    private val email: String,
    private val password: String,
    private val loginHelper: LoginHelper,
    private val register: RegisterService,
    private val student: StudentService,
    private val url: ServiceManager.UrlGenerator
) {

    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    private val json = Json {
        ignoreUnknownKeys = true
    }

    suspend fun getStudents(): List<Student> {
        return getSymbols().flatMap { (symbol, certificate, loginType) ->
            val cert = try {
                loginHelper.sendCertificate(certificate, email, certificate.action.replace(startSymbol.getNormalizedSymbol(), symbol))
            } catch (e: AccountPermissionException) {
                logger.debug("Certificate send failed", e)
                return@flatMap emptyList<Student>()
            }

            url.symbol = symbol

            val permissions = getPermissions(cert.document.toString())
            val units = permissions?.units.orEmpty().associateWith { unit ->
                permissions?.authInfos.orEmpty().find { it.unitId == unit.id }
            }.filterNot { (_, authInfo) ->
                authInfo?.studentIds.isNullOrEmpty() && authInfo?.parentIds.isNullOrEmpty()
            }

            logger.debug("${cert.studentSchools.map { it.attr("href") }}")

            units.flatMap { (unit, authInfo) ->
                getStudentsFromUnit(symbol, loginType, unit, authInfo, getUserNameFromUserData(cert.userData))
            }
        }.distinctBy { pupil -> listOf(pupil.studentId, pupil.classId, pupil.schoolSymbol) }
    }

    private suspend fun getSymbols(): List<Triple<String, CertificateResponse, Scrapper.LoginType>> {
        val symbolLoginType = getLoginType(startSymbol.getNormalizedSymbol())
        println("Register login type: $symbolLoginType")
        val cert = loginHelper.apply { loginType = symbolLoginType }.sendCredentials(email, password)

        return Jsoup.parse(cert.wresult.replace(":", ""), "", Parser.xmlParser())
            .select("[AttributeName$=\"Instance\"] samlAttributeValue")
            .map { it.text().trim() }
            .apply { logger.debug("$this") }
            .filter { it.matches("[a-zA-Z0-9]*".toRegex()) } // early filter invalid symbols
            .filter { it != "Default" }
            .apply { logger.debug("$this") }
            .map { Triple(it, cert, symbolLoginType) }
    }

    private suspend fun getLoginType(symbol: String): Scrapper.LoginType {
        return getLoginType(url.also { it.symbol = symbol })
    }

    private suspend fun getLoginType(urlGenerator: ServiceManager.UrlGenerator): Scrapper.LoginType {
        val page = register.getFormType(urlGenerator.generate(ServiceManager.UrlGenerator.Site.LOGIN) + "Account/LogOn").page
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

    private suspend fun getStudentsFromUnit(
        symbol: String,
        loginType: Scrapper.LoginType,
        unit: Unit,
        authInfo: AuthInfo?,
        userName: String?,
    ): List<Student> {
        url.schoolId = unit.symbol

        val studentStartPage = try {
            student.getStart(url.generate(ServiceManager.UrlGenerator.Site.STUDENT) + "Start")
        } catch (e: TemporarilyDisabledException) {
            logger.debug("Start page is unavailable", e)
            return listOf()
        }

        val cache = getStudentCache(studentStartPage)
        val diaries = getStudentDiaries()

        return diaries.filterDiaries().map { diary ->
            val classId = diary.semesters?.firstOrNull()?.classId ?: 0

            Student(
                email = email,
                userLogin = email,
                userName = (userName ?: email).trim(),
                userLoginId = authInfo?.loginId ?: diary.studentId,
                symbol = symbol,
                studentId = diary.studentId,
                studentName = diary.studentName.trim(),
                studentSecondName = diary.studentSecondName.orEmpty(),
                studentSurname = diary.studentSurname,
                schoolSymbol = unit.symbol,
                schoolShortName = unit.short,
                schoolName = getScriptParam("organizationName", studentStartPage, "${unit.name} ${unit.short}"),
                className = diary.symbol.orEmpty(),
                classId = classId,
                baseUrl = url.generate(ServiceManager.UrlGenerator.Site.BASE),
                loginType = loginType,
                isParent = cache?.isParent == true,
                semesters = diaries.toSemesters(diary.studentId, classId, authInfo?.loginId ?: diary.studentId),
            )
        }.ifEmpty {
            logger.debug("No supported student found in diaries: $diaries")
            emptyList()
        }
    }

    private suspend fun getStudentCache(startPage: String) = student.getUserCache(
        url.generate(ServiceManager.UrlGenerator.Site.STUDENT) + "UczenCache.mvc/Get",
        getScriptParam("antiForgeryToken", startPage),
        getScriptParam("appGuid", startPage),
        getScriptParam("version", startPage)
    ).data

    private suspend fun getStudentDiaries() = student
        .getSchoolInfo(url.generate(ServiceManager.UrlGenerator.Site.STUDENT) + "UczenDziennik.mvc/Get")
        .handleErrors()
        .data.orEmpty()

    private fun List<Diary>.filterDiaries() = this
        .filter { it.semesters.orEmpty().isNotEmpty() || it.kindergartenDiaryId != 0 }
        .sortedByDescending { it.level }
        .distinctBy { listOf(it.studentId, it.semesters?.firstOrNull()?.classId ?: 0) }

    private fun getPermissions(homepage: String): Permission? {
        val base64 = getScriptParam("permissions", homepage).substringBefore("|")
        return Base64.decode(base64).toString(StandardCharsets.UTF_8).takeIf { it.isNotBlank() }?.let {
            json.decodeFromString<Permission>(it)
        }
    }

    private fun getUserNameFromUserData(userData: String): String? {
        val adfsName = userData.substringBefore(" (", "")
        val standardName = userData.substringBefore(" - ", adfsName)
        return standardName.takeIf { it.isNotBlank() }
    }
}

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
import io.github.wulkanowy.sdk.scrapper.messages.ReportingUnit
import io.github.wulkanowy.sdk.scrapper.register.Diary
import io.github.wulkanowy.sdk.scrapper.register.Permission
import io.github.wulkanowy.sdk.scrapper.register.Student
import io.github.wulkanowy.sdk.scrapper.register.toSemesters
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository.Companion.SELECTOR_ADFS
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository.Companion.SELECTOR_ADFS_CARDS
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository.Companion.SELECTOR_ADFS_LIGHT
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository.Companion.SELECTOR_STANDARD
import io.github.wulkanowy.sdk.scrapper.service.MessagesService
import io.github.wulkanowy.sdk.scrapper.service.RegisterService
import io.github.wulkanowy.sdk.scrapper.service.ServiceManager
import io.github.wulkanowy.sdk.scrapper.service.StudentService
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.parser.Parser
import org.slf4j.LoggerFactory
import java.net.URL
import java.nio.charset.StandardCharsets

class RegisterRepository(
    private val startSymbol: String,
    private val email: String,
    private val password: String,
    private val loginHelper: LoginHelper,
    private val register: RegisterService,
    private val messages: MessagesService,
    private val student: StudentService,
    private val url: ServiceManager.UrlGenerator
) {

    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    suspend fun getStudents(): List<Student> {
        return getSymbols().flatMap { (symbol, certificate) ->
            val cert = try {
                loginHelper.sendCertificate(certificate, email, certificate.action.replace(startSymbol.getNormalizedSymbol(), symbol))
            } catch (e: AccountPermissionException) {
                logger.debug("Certificate send failed", e)
                return@flatMap emptyList<Student>()
            }

            url.symbol = symbol
            val unitsUrl = url.generate(ServiceManager.UrlGenerator.Site.MESSAGES) + "NowaWiadomosc.mvc/GetJednostkiUzytkownika"

            val units = messages.getUserReportingUnits(unitsUrl).handleErrors().data.orEmpty()
            val permissions = getPermissions(cert.document.toString())
            cert.studentSchools.flatMap { moduleUrl ->
                getStudentsBySymbol(symbol, moduleUrl, permissions, units)
            }
        }.distinctBy { pupil -> listOf(pupil.studentId, pupil.classId, pupil.schoolSymbol) }
    }

    private suspend fun getSymbols(): List<Pair<String, CertificateResponse>> {
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
            .map { it to cert }
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

    private suspend fun getStudentsBySymbol(
        symbol: String,
        moduleUrl: Element,
        permissions: Permission?,
        units: List<ReportingUnit>
    ): List<Student> {
        val loginType = getLoginType(symbol)
        val schoolUrl = moduleUrl.attr("href")
        url.schoolId = getExtractedSchoolSymbolFromUrl(schoolUrl)

        val studentStartPage = try {
            student.getStart(url.generate(ServiceManager.UrlGenerator.Site.STUDENT) + "Start")
        } catch (e: TemporarilyDisabledException) {
            logger.debug("Start page is unavailable", e)
            return listOf()
        }

        val cache = getStudentCache(studentStartPage)

        val diaries = getStudentDiaries()
        return diaries.filterDiaries().map { diary ->
            val unit = units.getUnitByStudentId(diary, url.schoolId, permissions)
            val schoolSymbol = getExtractedSchoolSymbolFromUrl(schoolUrl)
            val classId = diary.semesters?.firstOrNull()?.classId ?: 0

            Student(
                email = email,
                userLogin = email,
                userName = unit?.senderName ?: email,
                userLoginId = unit?.senderId ?: 0,
                symbol = symbol,
                studentId = diary.studentId,
                studentName = diary.studentName,
                studentSecondName = diary.studentSecondName.orEmpty(),
                studentSurname = diary.studentSurname,
                schoolSymbol = schoolSymbol,
                schoolShortName = moduleUrl.text().takeIf { "Uczeń" !in it }.orEmpty(),
                schoolName = getScriptParam("organizationName", studentStartPage, diary.symbol + " " + (diary.year - diary.level + 1)),
                className = diary.symbol.orEmpty(),
                classId = classId,
                baseUrl = url.generate(ServiceManager.UrlGenerator.Site.BASE),
                loginType = loginType,
                isParent = cache?.isParent == true,
                semesters = diaries.toSemesters(diary.studentId, classId, unit?.unitId ?: 0),
            )
        }.ifEmpty {
            logger.error("No supported student found in diaries: $diaries")
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

    private fun getExtractedSchoolSymbolFromUrl(snpPageUrl: String): String {
        val path = URL(snpPageUrl).path.split("/")
        return path[2]
    }

    private fun List<ReportingUnit>.getUnitByStudentId(diary: Diary, schoolId: String, permissions: Permission?): ReportingUnit? {
        val idFromPermissions = permissions?.units?.firstOrNull { it.symbol == schoolId }?.id
        val idFromSemesters = diary.semesters?.firstOrNull()?.unitId
        val unitId = idFromSemesters ?: idFromPermissions

        return firstOrNull { it.unitId == unitId }
    }

    private fun getPermissions(homepage: String): Permission? {
        val base64 = getScriptParam("permissions", homepage).substringBefore("|")
        return Base64.decode(base64).toString(StandardCharsets.UTF_8).takeIf { it.isNotBlank() }?.let {
            Json.decodeFromString<Permission>(it)
        }
    }
}

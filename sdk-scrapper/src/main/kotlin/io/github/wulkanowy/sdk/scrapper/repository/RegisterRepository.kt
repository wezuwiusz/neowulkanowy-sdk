package io.github.wulkanowy.sdk.scrapper.repository

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
import io.github.wulkanowy.sdk.scrapper.register.Student
import io.github.wulkanowy.sdk.scrapper.register.toSemesters
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository.Companion.SELECTOR_ADFS
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository.Companion.SELECTOR_ADFS_CARDS
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository.Companion.SELECTOR_ADFS_LIGHT
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository.Companion.SELECTOR_ADFS_MS
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository.Companion.SELECTOR_STANDARD
import io.github.wulkanowy.sdk.scrapper.service.MessagesService
import io.github.wulkanowy.sdk.scrapper.service.RegisterService
import io.github.wulkanowy.sdk.scrapper.service.ServiceManager
import io.github.wulkanowy.sdk.scrapper.service.StudentService
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.parser.Parser
import org.slf4j.LoggerFactory
import java.net.URL

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
                return@flatMap emptyList<Student>()
            }

            url.symbol = symbol
            val unitsUrl = url.generate(ServiceManager.UrlGenerator.Site.MESSAGES) + "NowaWiadomosc.mvc/GetJednostkiUzytkownika"
            val units = messages.getUserReportingUnits(unitsUrl).handleErrors().data.orEmpty()
            cert.studentSchools.flatMap { moduleUrl ->
                getStudentsBySymbol(symbol, moduleUrl, units)
            }
        }.distinctBy { pupil -> listOf(pupil.studentId, pupil.classId, pupil.schoolSymbol) }
    }

    private suspend fun getSymbols(): List<Pair<String, CertificateResponse>> {
        val symbolLoginType = getLoginType(startSymbol.getNormalizedSymbol())
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
            page.select(SELECTOR_ADFS).isNotEmpty() || page.select(SELECTOR_ADFS_MS).isNotEmpty() -> Scrapper.LoginType.ADFS
            page.select(SELECTOR_ADFS_LIGHT).isNotEmpty() -> {
                page.selectFirst("form").attr("action").run {
                    when {
                        contains("cufs.edu.lublin.eu") -> Scrapper.LoginType.ADFSLightCufs
                        startsWith("/LoginPage.aspx") -> Scrapper.LoginType.ADFSLight
                        startsWith("/${urlGenerator.symbol}/LoginPage.aspx") -> Scrapper.LoginType.ADFSLightScoped
                        else -> throw ScrapperException("Nieznany typ dziennika ADFS")
                    }
                }
            }
            page.select(SELECTOR_ADFS_CARDS).isNotEmpty() -> Scrapper.LoginType.ADFSCards
            else -> throw ScrapperException("Nieznany typ dziennika")
        }
    }

    private suspend fun getStudentsBySymbol(symbol: String, moduleUrl: Element, units: List<ReportingUnit>): List<Student> {
        val loginType = getLoginType(symbol)
        val schoolUrl = moduleUrl.attr("href")
        url.schoolId = getExtractedSchoolSymbolFromUrl(schoolUrl)

        val startPage = try {
            student.getStart(url.generate(ServiceManager.UrlGenerator.Site.STUDENT) + "Start")
        } catch (e: TemporarilyDisabledException) {
            return listOf()
        }

        val cache = getStudentCache(startPage)

        val diaries = getStudentDiaries()
        return diaries.filterDiaries().map { diary ->
            val schoolSymbol = getExtractedSchoolSymbolFromUrl(schoolUrl)
            val unit = units.firstOrNull { it.unitId == diary.semesters!![0].unitId }

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
                schoolName = getScriptParam("organizationName", startPage, diary.symbol + " " + (diary.year - diary.level + 1)),
                className = diary.symbol.orEmpty(),
                classId = diary.semesters!![0].classId,
                baseUrl = url.generate(ServiceManager.UrlGenerator.Site.BASE),
                loginType = loginType,
                isParent = cache?.isParent == true,
                semesters = diaries
                    .filter { it.studentId == diary.studentId && it.semesters?.getOrNull(0)?.classId == diary.semesters[0].classId }
                    .flatMap { it.toSemesters() }
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
        .filter { it.semesters.orEmpty().isNotEmpty() }
        .sortedByDescending { it.level }
        .distinctBy { listOf(it.studentId, it.semesters!![0].classId) }

    private fun getExtractedSchoolSymbolFromUrl(snpPageUrl: String): String {
        val path = URL(snpPageUrl).path.split("/")
        return path[2]
    }
}

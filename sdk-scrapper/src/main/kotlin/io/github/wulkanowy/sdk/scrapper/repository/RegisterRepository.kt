package io.github.wulkanowy.sdk.scrapper.repository

import io.github.wulkanowy.sdk.scrapper.Scrapper
import io.github.wulkanowy.sdk.scrapper.ScrapperException
import io.github.wulkanowy.sdk.scrapper.exception.TemporarilyDisabledException
import io.github.wulkanowy.sdk.scrapper.getNormalizedSymbol
import io.github.wulkanowy.sdk.scrapper.getScriptParam
import io.github.wulkanowy.sdk.scrapper.interceptor.handleErrors
import io.github.wulkanowy.sdk.scrapper.login.AccountPermissionException
import io.github.wulkanowy.sdk.scrapper.login.CertificateResponse
import io.github.wulkanowy.sdk.scrapper.login.LoginHelper
import io.github.wulkanowy.sdk.scrapper.register.SendCertificateResponse
import io.github.wulkanowy.sdk.scrapper.register.Student
import io.github.wulkanowy.sdk.scrapper.register.StudentAndParentResponse
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository.Companion.SELECTOR_ADFS
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository.Companion.SELECTOR_ADFS_CARDS
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository.Companion.SELECTOR_ADFS_LIGHT
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository.Companion.SELECTOR_STANDARD
import io.github.wulkanowy.sdk.scrapper.service.RegisterService
import io.github.wulkanowy.sdk.scrapper.service.ServiceManager
import io.github.wulkanowy.sdk.scrapper.service.StudentAndParentService
import io.github.wulkanowy.sdk.scrapper.service.StudentService
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import org.slf4j.LoggerFactory
import java.net.URL

class RegisterRepository(
    private val startSymbol: String,
    private val email: String,
    private val password: String,
    private val useNewStudent: Boolean,
    private val loginHelper: LoginHelper,
    private val register: RegisterService,
    private val snp: StudentAndParentService,
    private val student: StudentService,
    private val url: ServiceManager.UrlGenerator
) {

    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    suspend fun getStudents(): List<Student> {
        return getSymbols().map { (symbol, certificate) ->
            val cert = try {
                loginHelper.sendCertificate(certificate, email, certificate.action.replace(startSymbol.getNormalizedSymbol(), symbol))
            } catch (e: AccountPermissionException) {
                SendCertificateResponse()
            }
            (if (useNewStudent) cert.studentSchools else cert.oldStudentSchools).map { moduleUrl ->
                val loginType = getLoginType(symbol)
                getStudents(symbol, moduleUrl.attr("href")).map { student ->
                    Student(
                        email = email,
                        symbol = symbol,
                        studentId = student.id,
                        studentName = student.name,
                        schoolSymbol = getExtractedSchoolSymbolFromUrl(moduleUrl.attr("href")),
                        schoolShortName = moduleUrl.text().takeIf { "Uczeń" !in it }.orEmpty(),
                        schoolName = student.description,
                        className = student.className,
                        classId = student.classId,
                        baseUrl = url.generate(ServiceManager.UrlGenerator.Site.BASE),
                        loginType = loginType,
                        isParent = student.isParent
                    )
                }
            }.flatten()
        }.flatten().distinctBy { pupil -> listOf(pupil.studentId, pupil.classId, pupil.schoolSymbol) }
    }

    private suspend fun getSymbols(): List<Pair<String, CertificateResponse>> {
        val symbolLoginType = getLoginType(startSymbol.getNormalizedSymbol())
        val cert = loginHelper.apply { loginType = symbolLoginType }.sendCredentials(email, password)

        return Jsoup.parse(cert.wresult.replace(":", ""), "", Parser.xmlParser())
            .select("[AttributeName$=\"Instance\"] samlAttributeValue")
            .map { it.text().trim() }
            .apply { logger.debug("$this") }
            .filter { it.matches("[a-zA-Z0-9]*".toRegex()) } // early filter invalid symbols
            .map { Pair(it, cert) }
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

    private suspend fun getStudents(symbol: String, schoolUrl: String): List<StudentAndParentResponse.Student> {
        url.schoolId = getExtractedSchoolSymbolFromUrl(schoolUrl)
        url.symbol = symbol
        return if (!useNewStudent) {
            val res = snp.getSchoolInfo(schoolUrl)
            res.students.map { student ->
                student.apply {
                    description = res.schoolName
                    className = res.diaries[0].name
                }
            }
        } else {
            val startPage = try {
                student.getStart(url.generate(ServiceManager.UrlGenerator.Site.STUDENT) + "Start")
            } catch (e: TemporarilyDisabledException) {
                return listOf()
            }

            val cache = student.getUserCache(
                url.generate(ServiceManager.UrlGenerator.Site.STUDENT) + "UczenCache.mvc/Get",
                getScriptParam("antiForgeryToken", startPage),
                getScriptParam("appGuid", startPage),
                getScriptParam("version", startPage)
            )

            val diaries = student
                .getSchoolInfo(url.generate(ServiceManager.UrlGenerator.Site.STUDENT) + "UczenDziennik.mvc/Get")
                .handleErrors()
                .data.orEmpty()

            diaries.filter { diary -> diary.semesters?.isNotEmpty() ?: false }
                .sortedByDescending { diary -> diary.level }
                .distinctBy { diary -> listOf(diary.studentId, diary.semesters!![0].classId) }
                .map {
                    StudentAndParentResponse.Student().apply {
                        id = it.studentId
                        name = "${it.studentName} ${it.studentSurname}"
                        description = getScriptParam("organizationName", startPage, it.symbol + " " + (it.year - it.level + 1))
                        className = it.level.toString() + it.symbol
                        classId = it.semesters!![0].classId
                        isParent = cache.data?.isParent ?: false
                    }
                }
                .ifEmpty {
                    logger.debug("No supported student found: $diaries")
                    emptyList()
                }
        }
    }

    private fun getExtractedSchoolSymbolFromUrl(snpPageUrl: String): String {
        val path = URL(snpPageUrl).path.split("/")

        if (6 != path.size && !useNewStudent) {
            throw ScrapperException("Na pewno używasz konta z dostępem do Witryny ucznia i rodzica?")
        }

        return path[2]
    }
}

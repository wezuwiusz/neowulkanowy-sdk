package io.github.wulkanowy.sdk.scrapper.repository

import io.github.wulkanowy.sdk.scrapper.Scrapper
import io.github.wulkanowy.sdk.scrapper.ScrapperException
import io.github.wulkanowy.sdk.scrapper.getNormalizedSymbol
import io.github.wulkanowy.sdk.scrapper.getScriptParam
import io.github.wulkanowy.sdk.scrapper.interceptor.ErrorHandlerTransformer
import io.github.wulkanowy.sdk.scrapper.login.AccountPermissionException
import io.github.wulkanowy.sdk.scrapper.login.CertificateResponse
import io.github.wulkanowy.sdk.scrapper.login.LoginHelper
import io.github.wulkanowy.sdk.scrapper.register.SendCertificateResponse
import io.github.wulkanowy.sdk.scrapper.register.Student
import io.github.wulkanowy.sdk.scrapper.register.StudentAndParentResponse
import io.github.wulkanowy.sdk.scrapper.service.RegisterService
import io.github.wulkanowy.sdk.scrapper.service.ServiceManager
import io.github.wulkanowy.sdk.scrapper.service.StudentAndParentService
import io.github.wulkanowy.sdk.scrapper.service.StudentService
import io.reactivex.Observable
import io.reactivex.Single
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

    fun getStudents(): Single<List<Student>> {
        return getSymbols().flatMapObservable { Observable.fromIterable(it) }.flatMap { (symbol, certificate) ->
            loginHelper.sendCertificate(certificate, email, certificate.action.replace(startSymbol.getNormalizedSymbol(), symbol))
                .onErrorResumeNext { t ->
                    if (t is AccountPermissionException) Single.just(SendCertificateResponse())
                    else Single.error(t)
                }
                .flatMapObservable { res ->
                    Observable.fromIterable(if (useNewStudent) res.studentSchools else res.oldStudentSchools).flatMapSingle { moduleUrl ->
                        getLoginType(symbol).flatMap { loginType ->
                            getStudents(symbol, moduleUrl).map { students ->
                                students.map { student ->
                                    Student(
                                        email = email,
                                        symbol = symbol,
                                        studentId = student.id,
                                        studentName = student.name,
                                        schoolSymbol = getExtractedSchoolSymbolFromUrl(moduleUrl),
                                        schoolName = student.description,
                                        className = student.className,
                                        classId = student.classId,
                                        baseUrl = url.generate(ServiceManager.UrlGenerator.Site.BASE),
                                        loginType = loginType,
                                        isParent = student.isParent
                                    )
                                }
                            }
                        }
                    }
                }
        }.toList().map {
            it.flatten().distinctBy { pupil ->
                listOf(pupil.studentId, pupil.classId, pupil.schoolSymbol)
            }
        }
    }

    private fun getSymbols(): Single<List<Pair<String, CertificateResponse>>> {
        return getLoginType(startSymbol.getNormalizedSymbol()).map {
            loginHelper.apply { loginType = it }
        }.flatMap { login ->
            login.sendCredentials(email, password).flatMap { Single.just(it) }.flatMap { cert ->
                Single.just(Jsoup.parse(cert.wresult.replace(":", ""), "", Parser.xmlParser())
                    .select("[AttributeName$=\"Instance\"] samlAttributeValue")
                    .map { it.text().trim() }
                    .apply { logger.debug("$this") }
                    .filter { it.matches("[a-zA-Z0-9]*".toRegex()) } // early filter invalid symbols
                    .ifEmpty { listOf("opole", "gdansk", "tarnow", "rzeszow") } // fallback
                    .map { Pair(it, cert) }
                )
            }
        }
    }

    private fun getLoginType(symbol: String): Single<Scrapper.LoginType> {
        return register.getFormType("/$symbol/Account/LogOn").map { it.page }.map {
            when {
                it.select(".LogOnBoard input[type=submit]").isNotEmpty() -> Scrapper.LoginType.STANDARD
                it.select("form[name=form1] #SubmitButton").isNotEmpty() -> Scrapper.LoginType.ADFS
                it.select(".submit-button, form #SubmitButton").isNotEmpty() -> {
                    it.selectFirst("form").attr("action").run {
                        when {
                            contains("cufs.edu.lublin.eu") -> Scrapper.LoginType.ADFSLightCufs
                            startsWith("/LoginPage.aspx") -> Scrapper.LoginType.ADFSLight
                            startsWith("/$symbol/LoginPage.aspx") -> Scrapper.LoginType.ADFSLightScoped
                            else -> throw ScrapperException("Nieznany typ dziennika ADFS")
                        }
                    }
                }
                it.select("#PassiveSignInButton").isNotEmpty() -> Scrapper.LoginType.ADFSCards
                else -> throw ScrapperException("Nieznany typ dziennika")
            }
        }
    }

    private fun getStudents(symbol: String, schoolUrl: String): Single<List<StudentAndParentResponse.Student>> {
        url.schoolId = getExtractedSchoolSymbolFromUrl(schoolUrl)
        url.symbol = symbol
        return if (!useNewStudent) snp.getSchoolInfo(schoolUrl).map { res ->
            res.students.map { student ->
                student.apply {
                    description = res.schoolName
                    className = res.diaries[0].name
                }
            }
        } else student.getSchoolInfo(url.generate(ServiceManager.UrlGenerator.Site.STUDENT) + "UczenDziennik.mvc/Get")
            .compose(ErrorHandlerTransformer())
            .map { it.data }
            .map { it.filter { diary -> diary.semesters?.isNotEmpty() ?: false } }
            .map { it.sortedByDescending { diary -> diary.level } }
            .map { diary -> diary.distinctBy { listOf(it.studentId, it.semesters!![0].classId) } }
            .flatMap { diaries ->
                student.getStart(url.generate(ServiceManager.UrlGenerator.Site.STUDENT) + "Start").flatMap { startPage ->
                    student.getUserCache(
                        url.generate(ServiceManager.UrlGenerator.Site.STUDENT) + "UczenCache.mvc/Get",
                        getScriptParam("antiForgeryToken", startPage),
                        getScriptParam("appGuid", startPage),
                        getScriptParam("version", startPage)
                    ).map { cache ->
                        diaries.map {
                            StudentAndParentResponse.Student().apply {
                                id = it.studentId
                                name = "${it.studentName} ${it.studentSurname}"
                                description = getScriptParam("organizationName", startPage, it.symbol + " " + (it.year - it.level + 1))
                                className = it.level.toString() + it.symbol
                                classId = it.semesters!![0].classId
                                isParent = cache.data?.isParent ?: false
                            }
                        }
                    }
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

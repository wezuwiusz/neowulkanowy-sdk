package io.github.wulkanowy.api.repository

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.api.ApiException
import io.github.wulkanowy.api.getScriptParam
import io.github.wulkanowy.api.login.AccountPermissionException
import io.github.wulkanowy.api.login.CertificateResponse
import io.github.wulkanowy.api.login.LoginHelper
import io.github.wulkanowy.api.register.SendCertificateResponse
import io.github.wulkanowy.api.register.Student
import io.github.wulkanowy.api.register.StudentAndParentResponse
import io.github.wulkanowy.api.service.RegisterService
import io.github.wulkanowy.api.service.ServiceManager
import io.github.wulkanowy.api.service.StudentAndParentService
import io.github.wulkanowy.api.service.StudentService
import io.reactivex.Observable
import io.reactivex.Single
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import java.net.URL
import java.text.Normalizer

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

    fun getStudents(): Single<List<Student>> {
        return getSymbols().flatMapObservable { Observable.fromIterable(it) }.flatMap { (symbol, certificate) ->
            loginHelper.sendCertificate(certificate, certificate.action.replace(startSymbol.getNormalizedSymbol(), symbol))
                .onErrorResumeNext { t ->
                    if (t is AccountPermissionException) Single.just(SendCertificateResponse())
                    else Single.error(t)
                }
                .flatMapObservable { switchLogin(it, symbol) }
                .flatMap { res ->
                    Observable.fromIterable(if (useNewStudent) res.studentSchools else res.oldStudentSchools).flatMapSingle { moduleUrl ->
                        getLoginType(symbol).flatMap { loginType ->
                            getStudents(symbol, moduleUrl).map { students ->
                                students.map { student ->
                                    Student(
                                        email = if (email == res.currentEmail) email else "$email||${res.currentEmail}",
                                        symbol = symbol,
                                        studentId = student.id,
                                        studentName = student.name,
                                        schoolSymbol = getExtractedSchoolSymbolFromUrl(moduleUrl),
                                        schoolName = student.description,
                                        className = student.className,
                                        classId = student.classId,
                                        loginType = loginType
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
                    .filter { it.matches("[a-zA-Z]*".toRegex()) } // early filter invalid symbols
                    .ifEmpty { listOf("opole", "gdansk", "tarnow", "rzeszow") } // fallback
                    .map { Pair(it, cert) }
                )
            }
        }
    }

    private fun getLoginType(symbol: String): Single<Api.LoginType> {
        return register.getFormType("/$symbol/Account/LogOn").map { it.page }.map {
            when {
                it.select(".LogOnBoard input[type=submit]").isNotEmpty() -> Api.LoginType.STANDARD
                it.select("form[name=form1] #SubmitButton").isNotEmpty() -> Api.LoginType.ADFS
                it.select("form #SubmitButton").isNotEmpty() -> {
                    it.selectFirst("form").attr("action").run {
                        when {
                            startsWith("/LoginPage.aspx") -> Api.LoginType.ADFSLight
                            startsWith("/$symbol/LoginPage.aspx") -> Api.LoginType.ADFSLightScoped
                            else -> throw ApiException("Nieznany typ dziennika ADFS")
                        }
                    }
                }
                it.select("#PassiveSignInButton").isNotEmpty() -> Api.LoginType.ADFSCards
                else -> throw ApiException("Nieznany typ dziennika")
            }
        }
    }

    private fun switchLogin(homeResponse: SendCertificateResponse, symbol: String): Observable<SendCertificateResponse> {
        if (homeResponse.emails.isEmpty()) return Observable.just(homeResponse)

        return Single.just(homeResponse.emails).flatMapObservable { Observable.fromIterable(listOf(email).union(it)) }.flatMapSingle { item ->
            (if (item == email) Single.just(homeResponse) else loginHelper.switchLogin(item, symbol)).map {
                it.apply {
                    currentEmail = item
                }
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
            .map { it.data }
            .map { it.filter { diary -> diary.semesters?.isNotEmpty() ?: false } }
            .map { it.sortedByDescending { diary -> diary.level } }
            .map { diary -> diary.distinctBy { listOf(it.studentId, it.semesters!![0].classId) } }
            .flatMap { diaries ->
                student.getStart(url.generate(ServiceManager.UrlGenerator.Site.STUDENT) + "Start").map { startPage ->
                    diaries.map {
                        StudentAndParentResponse.Student().apply {
                            id = it.studentId
                            name = "${it.studentName} ${it.studentSurname}"
                            description = getScriptParam("organizationName", startPage, it.symbol + " " + (it.year - it.level + 1))
                            className = it.level.toString() + it.symbol
                            classId = it.semesters!![0].classId
                        }
                    }
                }
            }
    }

    private fun getExtractedSchoolSymbolFromUrl(snpPageUrl: String): String {
        val path = URL(snpPageUrl).path.split("/")

        if (6 != path.size && !useNewStudent) {
            throw ApiException("Na pewno używasz konta z dostępem do Witryny ucznia i rodzica?")
        }

        return path[2]
    }

    private fun String.getNormalizedSymbol(): String {
        return trim().toLowerCase().replace("default", "").run {
            Normalizer.normalize(this, Normalizer.Form.NFD).run {
                "\\p{InCombiningDiacriticalMarks}+".toRegex().replace(this, "")
            }
        }.replace("[^a-z]".toRegex(), "").ifBlank { "Default" }
    }
}

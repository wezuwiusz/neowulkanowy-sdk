package io.github.wulkanowy.api.repository

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.api.ApiException
import io.github.wulkanowy.api.login.AccountPermissionException
import io.github.wulkanowy.api.login.CertificateResponse
import io.github.wulkanowy.api.login.LoginHelper
import io.github.wulkanowy.api.register.SendCertificateResponse
import io.github.wulkanowy.api.register.Pupil
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

    fun getPupils(): Single<List<Pupil>> {
        return getSymbols().flatMapObservable { Observable.fromIterable(it) }.flatMap { symbol ->
            loginHelper.sendCertificate(symbol.second, symbol.second.action.replace(startSymbol, symbol.first))
                    .onErrorResumeNext { t ->
                        if (t is AccountPermissionException) Single.just(SendCertificateResponse())
                        else Single.error(t)
                    }
                    .flatMapObservable { Observable.fromIterable(if (useNewStudent) it.studentSchools else it.oldStudentSchools) }
                    .flatMapSingle { schoolUrl ->
                        getLoginType(symbol.first).flatMap { loginType ->
                            getStudents(symbol.first, schoolUrl).map {
                                it.map { pupil ->
                                    Pupil(
                                            email = email,
                                            symbol = symbol.first,
                                            studentId = pupil.id,
                                            studentName = pupil.name,
                                            schoolSymbol = getExtractedSchoolSymbolFromUrl(schoolUrl),
                                            description = pupil.description,
                                            schoolName = pupil.description,
                                            loginType = loginType
                                    )
                                }
                            }
                        }
                    }
        }.toList().map { it.flatten().distinctBy { pupil -> pupil.studentId to pupil.schoolSymbol } }
    }

    private fun getStudents(symbol: String, schoolUrl: String): Single<List<StudentAndParentResponse.Pupil>> {
        url.schoolId = getExtractedSchoolSymbolFromUrl(schoolUrl)
        url.symbol = symbol
        return if (!useNewStudent) snp.getSchoolInfo(schoolUrl).map { res ->
            res.students.map { pupil ->
                pupil.apply {
                    description = res.schoolName
                }
            }
        } else student.getSchoolInfo(url.generate(ServiceManager.UrlGenerator.Site.STUDENT) + "UczenDziennik.mvc/Get")
                .map { diary -> diary.data?.distinctBy { it.studentId } }
                .map { diaries ->
                    diaries.map {
                        StudentAndParentResponse.Pupil().apply {
                            id = it.studentId
                            name = "${it.studentName} ${it.studentSurname}"
                            description = it.symbol + " " + (it.year - it.level + 1)
                        }
                    }
                }
    }

    private fun getSymbols(): Single<List<Pair<String, CertificateResponse>>> {
        return getLoginType(startSymbol).map {
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
        return register.getFormType("/$symbol/Account/LogOn").map {
            when {
                it.page.select(".LogOnBoard input[type=submit]").isNotEmpty() -> Api.LoginType.STANDARD
                it.page.select("form[name=form1] #SubmitButton").isNotEmpty() -> Api.LoginType.ADFS
                it.page.select("form #SubmitButton").isNotEmpty() -> Api.LoginType.ADFSLight
                it.page.select("#PassiveSignInButton").isNotEmpty() -> Api.LoginType.ADFSCards
                else -> throw ApiException("Nieznany typ dziennika")
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
}

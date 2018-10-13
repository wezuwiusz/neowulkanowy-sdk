package io.github.wulkanowy.api.repository

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.api.ApiException
import io.github.wulkanowy.api.login.AccountPermissionException
import io.github.wulkanowy.api.login.CertificateResponse
import io.github.wulkanowy.api.register.HomepageResponse
import io.github.wulkanowy.api.register.Pupil
import io.github.wulkanowy.api.service.RegisterService
import io.github.wulkanowy.api.service.StudentAndParentService
import io.reactivex.Observable
import io.reactivex.Single
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import java.net.URL

class RegisterRepository(
        private val startSymbol: String,
        private val email: String,
        private val password: String,
        private val loginRepo: LoginRepository,
        private val register: RegisterService,
        private val api: StudentAndParentService
) {

    fun getPupils(): Single<List<Pupil>> {
        return getSymbols().flatMapObservable { Observable.fromIterable(it) }.flatMap { symbol ->
            loginRepo.sendCertificate(symbol.second, symbol.second.action.replace(startSymbol, symbol.first))
                    .onErrorResumeNext { t ->
                        if (t is AccountPermissionException) Single.just(HomepageResponse())
                        else Single.error(t)
                    }
                    .flatMapObservable { Observable.fromIterable(it.schools) }
                    .flatMapSingle { schoolUrl ->
                        getLoginType().flatMap { loginType ->
                        api.getSchoolInfo(schoolUrl).map {
                                it.students.map { pupil ->
                                    Pupil(
                                            email = email,
                                            symbol = symbol.first,
                                            studentId = pupil.id,
                                            studentName = pupil.name,
                                            schoolId = getExtractedIdFromUrl(schoolUrl),
                                            schoolName = it.schoolName,
                                            loginType = loginType
                                    )
                                }
                            }
                        }
                    }
        }.toList().map { it.flatten() }
    }

    private fun getSymbols(): Single<List<Pair<String, CertificateResponse>>> {
        return getLoginType().map {
            loginRepo.apply { loginType = it }
        }.flatMap { login ->
            login.sendCredentials(email, password).flatMap { Single.just(it) }.flatMap { cert ->
                Single.just(Jsoup.parse(cert.wresult.replace(":", ""), "", Parser.xmlParser())
                        .select("[AttributeName$=\"Instance\"] samlAttributeValue")
                        .map { Pair(it.text(), cert) }
                )
            }
        }
    }

    private fun getLoginType(): Single<Api.LoginType> {
        return register.getFormType().map {
            when {
                it.page.select(".LogOnBoard input[type=submit]").isNotEmpty() -> Api.LoginType.STANDARD
                it.page.select("#SubmitButton").isNotEmpty() -> Api.LoginType.ADFSLight
                it.page.select("#PassiveSignInButton").isNotEmpty() -> Api.LoginType.ADFS
                else -> throw ApiException("Nieznany typ dziennika")
            }
        }
    }

    private fun getExtractedIdFromUrl(snpPageUrl: String): String {
        val path = URL(snpPageUrl).path.split("/")

        if (6 != path.size) {
            throw ApiException("Na pewno używasz konta z dostępem do Witryny ucznia i rodzica?")
        }

        return path[2]
    }
}

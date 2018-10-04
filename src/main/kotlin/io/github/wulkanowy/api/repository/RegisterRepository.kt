package io.github.wulkanowy.api.repository

import io.github.wulkanowy.api.ApiException
import io.github.wulkanowy.api.login.AccountPermissionException
import io.github.wulkanowy.api.login.CertificateResponse
import io.github.wulkanowy.api.register.HomepageResponse
import io.github.wulkanowy.api.register.Pupil
import io.github.wulkanowy.api.service.StudentAndParentService
import io.reactivex.Observable
import io.reactivex.Single
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import java.net.URL

class RegisterRepository(
        private val globalSymbol: String,
        private val email: String,
        private val password: String,
        private val loginRepo: LoginRepository,
        private val api: StudentAndParentService
) {

    fun getPupils(): Single<List<Pupil>> {
        return getSymbols().flatMapObservable { Observable.fromIterable(it) }.flatMap { symbol ->
            loginRepo.sendCertificate(symbol.second, symbol.second.action.replace(globalSymbol, symbol.first))
                    .onErrorResumeNext { t ->
                        if (t is AccountPermissionException) Single.just(HomepageResponse())
                        else Single.error(t)
                    }
                    .flatMapObservable { Observable.fromIterable(it.schools) }
                    .flatMapSingle { schoolUrl ->
                        api.getSchoolInfo(schoolUrl).map {
                            it.students.map { pupil ->
                                Pupil(
                                        email = email,
                                        symbol = symbol.first,
                                        studentId = pupil.id,
                                        studentName = pupil.name,
                                        schoolId = getExtractedIdFromUrl(schoolUrl),
                                        schoolName = it.schoolName
                                )
                            }
                        }
                    }
        }.toList().map { it.flatten() }
    }

    private fun getSymbols(): Single<List<Pair<String, CertificateResponse>>> {
        return loginRepo.sendCredentials(email, password).flatMap { Single.just(it) }.flatMap { cert ->
            Single.just(Jsoup.parse(cert.wresult.replace(":", ""), "", Parser.xmlParser())
                    .select("[AttributeName$=\"Instance\"] samlAttributeValue")
                    .map { Pair(it.text(), cert) }
            )
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

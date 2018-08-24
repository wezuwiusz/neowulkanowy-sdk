package io.github.wulkanowy.api.repository

import io.github.wulkanowy.api.auth.AccountPermissionException
import io.github.wulkanowy.api.login.CertificateResponse
import io.github.wulkanowy.api.register.Pupil
import io.reactivex.Single
import org.jsoup.Jsoup
import org.jsoup.parser.Parser

class RegisterRepository(
        private val globalSymbol: String,
        private val email: String,
        private val password: String,
        private val loginRepo: LoginRepository
) {

    private val homepageRepository by lazy { HomepageRepository(loginRepo.schema, loginRepo.host, loginRepo.client) }

    fun getPupils(): Single<List<Pupil>> {
        if (loginRepo.isADFS()) throw NotImplementedError()

        return Single.just(getSymbols().mapNotNull { symbol ->
            try {
                loginRepo.sendCertificate(symbol.second, symbol.second.action.replace(globalSymbol, symbol.first)).blockingGet()
                homepageRepository.getStartInfo(symbol.first).blockingGet().map { schoolId ->
                    val snpInfo = getSnpRepo(symbol.first, schoolId).getSchoolInfo().blockingGet()
                    snpInfo.students.map { pupil ->
                        Pupil(
                                email = email,
                                symbol = symbol.first,
                                studentId = pupil.id,
                                studentName = pupil.name,
                                schoolId = schoolId,
                                schoolName = snpInfo.schoolName
                        )
                    }
                }.flatten()
            } catch (e: RuntimeException) {
               if (e.cause !is AccountPermissionException) throw e
                null
            }
        }.flatten())
    }

    private fun getSymbols(): List<Pair<String, CertificateResponse>> {
        val cert = loginRepo.sendCredentials(mapOf("LoginName" to email, "Password" to password)).blockingGet()
        return Jsoup.parse(cert.wresult.replace(":", ""), "", Parser.xmlParser())
                .select("[AttributeName=\"UserInstance\"] samlAttributeValue")
                .map { Pair(it.text(), cert) }
    }

    private fun getSnpRepo(symbol: String, schoolId: String): StudentAndParentRepository {
        return StudentAndParentRepository(loginRepo.schema, loginRepo.host, symbol, schoolId, loginRepo.client)
    }
}

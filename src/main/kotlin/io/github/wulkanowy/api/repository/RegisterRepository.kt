package io.github.wulkanowy.api.repository

import io.github.wulkanowy.api.auth.AccountPermissionException
import io.github.wulkanowy.api.login.CertificateResponse
import io.github.wulkanowy.api.register.Pupil
import io.reactivex.Single
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import retrofit2.HttpException

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
                    getSnpRepo(symbol.first, schoolId).getInfo().blockingGet().map { pupil ->
                        Pupil(
                                email = email,
                                symbol = symbol.first,
                                studentId = pupil.id,
                                studentName = pupil.name,
                                schoolId = schoolId,
                                schoolName = pupil.schoolName,
                                diaryId = pupil.diaryId,
                                diaryName = pupil.diaryName,
                                semesterId = pupil.semesterId,
                                semesterNumber = pupil.semesterNumber
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

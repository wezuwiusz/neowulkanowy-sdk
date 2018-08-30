package io.github.wulkanowy.api.repository

import io.github.wulkanowy.api.login.CertificateResponse
import io.github.wulkanowy.api.register.HomepageResponse
import io.github.wulkanowy.api.service.LoginService
import io.reactivex.Single
import java.net.URLEncoder

class LoginRepository(
        private val schema: String,
        val host: String,
        private val symbol: String,
        private val api: LoginService
) {

    private val firstEndpointUrl by lazy {
        val url = URLEncoder.encode("$schema://uonetplus.$host/$symbol/LoginEndpoint.aspx", "UTF-8")
        "/$symbol/FS/LS?wa=wsignin1.0&wtrealm=$url&wctx=$url"
    }

    private fun isADFS(): Boolean {
        return when(host) {
            "vulcan.net.pl" -> false
            "fakelog.cf" -> false
            "fakelog.localhost:3000" -> false
            else -> true
        }
    }

    fun login(email: String, password: String): Single<HomepageResponse> {
        return sendCredentials(email, password).flatMap {
            sendCertificate(it)
        }
    }

    fun sendCredentials(email: String, password: String): Single<CertificateResponse> {
        return if (!isADFS()) api.sendCredentials(firstEndpointUrl, mapOf(
                "LoginName" to email,
                "Password" to password)
        ) else api.getForm(firstEndpointUrl).flatMap {
            api.sendADFSFormStandardChoice("$schema://adfs.$host/${it.formAction.removePrefix("/")}", mapOf(
                    "__db" to it.db,
                    "__VIEWSTATE" to it.viewstate,
                    "__VIEWSTATEGENERATOR" to it.viewstateGenerator,
                    "__EVENTVALIDATION" to it.eventValidation,
                    "PassiveSignInButton.x" to "0",
                    "PassiveSignInButton.y" to "0"
            ))
        }.flatMap {
            api.sendADFSCredentials("$schema://adfs.$host/${it.formAction.removePrefix("/")}", mapOf(
                    "__db" to it.db,
                    "__VIEWSTATE" to it.viewstate,
                    "__VIEWSTATEGENERATOR" to it.viewstateGenerator,
                    "__EVENTVALIDATION" to it.eventValidation,
                    "SubmitButton.x" to "0",
                    "SubmitButton.y" to "0",
                    "UsernameTextBox" to email,
                    "PasswordTextBox" to password
            ))
        }.flatMap {
            api.sendADFSFirstCertificate(it.action, mapOf(
                    "wa" to it.wa,
                    "wresult" to it.wresult,
                    "wctx" to it.wctx
            ))
        }
    }

    fun sendCertificate(certificate: CertificateResponse, url: String = certificate.action): Single<HomepageResponse> {
        return api.sendCertificate(url, mapOf(
                "wa" to certificate.wa,
                "wresult" to certificate.wresult,
                "wctx" to certificate.wctx
        ))
    }
}

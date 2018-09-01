package io.github.wulkanowy.api.repository

import io.github.wulkanowy.api.login.CertificateResponse
import io.github.wulkanowy.api.register.HomepageResponse
import io.github.wulkanowy.api.service.LoginService
import io.reactivex.Single
import java.net.URLEncoder

class LoginRepository(
        private val schema: String,
        private val host: String,
        private val symbol: String,
        private val api: LoginService
) {

    enum class LoginType {
        ADFS, ADFSLight, STANDARD
    }

    private val firstEndpointUrl by lazy {
        val url = URLEncoder.encode("$schema://uonetplus.$host/$symbol/LoginEndpoint.aspx", "UTF-8")
        "/$symbol/FS/LS?wa=wsignin1.0&wtrealm=$url&wctx=$url"
    }

    private val adfsLight by lazy {
        "$schema://adfslight.$host/LoginPage.aspx?ReturnUrl=/?wa=wsignin1.0&wtrealm=" +
                URLEncoder.encode("$schema://cufs.$host/Default/Account/LogOn&wctx=rm=0&id=ADFS&ru=/Default", "UTF-8")
    }

    private fun getType(): LoginType {
        return when (host) {
            // ADFS
            "eszkola.opolskie.pl" -> LoginType.ADFS
            "umt.tarnow.pl" -> LoginType.ADFS

            // ADFSLight
            "resman.pl" -> LoginType.ADFSLight

            // standard
            else -> LoginType.STANDARD
        }
    }

    fun login(email: String, password: String, type: LoginType = getType()): Single<HomepageResponse> {
        return sendCredentials(email, password, type).flatMap {
            sendCertificate(it)
        }
    }

    fun sendCredentials(email: String, password: String, type: LoginType = getType()): Single<CertificateResponse> {
        return when (type) {
            LoginType.ADFS -> api.getForm(firstEndpointUrl).flatMap {
                api.sendADFSFormStandardChoice("$schema://adfs.$host/${it.formAction.removePrefix("/")}", mapOf(
                        "__db" to it.db,
                        "__VIEWSTATE" to it.viewstate,
                        "__VIEWSTATEGENERATOR" to it.viewstateGenerator,
                        "__EVENTVALIDATION" to it.eventValidation,
                        "PassiveSignInButton.x" to "0",
                        "PassiveSignInButton.y" to "0"
                ))
            }.flatMap {
                api.sendADFSForm("$schema://adfs.$host/${it.formAction.removePrefix("/")}", mapOf(
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
                api.sendADFSForm(it.action, mapOf(
                        "wa" to it.wa,
                        "wresult" to it.wresult,
                        "wctx" to it.wctx
                ))
            }
            LoginType.ADFSLight -> api.sendADFSForm(adfsLight, mapOf(
                    "Username" to email,
                    "Password" to email
            ))
            else -> api.sendCredentials(firstEndpointUrl, mapOf(
                    "LoginName" to email,
                    "Password" to password
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

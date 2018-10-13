package io.github.wulkanowy.api.repository

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.api.ApiException
import io.github.wulkanowy.api.login.CertificateResponse
import io.github.wulkanowy.api.register.HomepageResponse
import io.github.wulkanowy.api.service.LoginService
import io.reactivex.Single
import java.net.URLEncoder

class LoginRepository(
        var loginType: Api.LoginType,
        private val schema: String,
        private val host: String,
        private val symbol: String,
        private val api: LoginService
) {

    private val firstStepReturnUrl by lazy {
        val url = URLEncoder.encode("$schema://uonetplus.$host/$symbol/LoginEndpoint.aspx", "UTF-8")
        "/$symbol/FS/LS?wa=wsignin1.0&wtrealm=$url&wctx=$url"
    }

    fun login(email: String, password: String): Single<HomepageResponse> {
        return sendCredentials(email, password).flatMap {
            sendCertificate(it)
        }
    }

    fun sendCredentials(email: String, password: String): Single<CertificateResponse> {
        return when (loginType) {
            Api.LoginType.AUTO -> throw ApiException("You must first specify LoginType before logging in")

            Api.LoginType.STANDARD -> api.sendCredentials(firstStepReturnUrl, mapOf(
                    "LoginName" to email,
                    "Password" to password
            ))

            Api.LoginType.ADFSLight -> api.getADFSLightForm("$schema://cufs.$host/$symbol/").flatMap {
                api.sendADFSForm("$schema://adfslight.$host/${it.formAction.removePrefix("/")}", mapOf(
                        "Username" to email,
                        "Password" to email
                ))
            }

            Api.LoginType.ADFS -> api.getForm(firstStepReturnUrl).flatMap {
                api.sendADFSForm("$schema://adfs.$host/${it.formAction.removePrefix("/")}", mapOf(
                        "__db" to it.db,
                        "__VIEWSTATE" to it.viewstate,
                        "__VIEWSTATEGENERATOR" to it.viewstateGenerator,
                        "__EVENTVALIDATION" to it.eventValidation,
                        "UsernameTextBox" to email,
                        "PasswordTextBox" to password,
                        "SubmitButton.x" to "0",
                        "SubmitButton.y" to "0"
                ))
            }.flatMap {
                api.sendADFSForm(it.action, mapOf(
                        "wa" to it.wa,
                        "wresult" to it.wresult,
                        "wctx" to it.wctx
                ))
            }

            Api.LoginType.ADFSCards -> api.getForm(firstStepReturnUrl).flatMap {
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

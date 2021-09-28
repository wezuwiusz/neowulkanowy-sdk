package io.github.wulkanowy.sdk.scrapper.login

import io.github.wulkanowy.sdk.scrapper.Scrapper
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFS
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFSCards
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFSLight
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFSLightCufs
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFSLightScoped
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.AUTO
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.STANDARD
import io.github.wulkanowy.sdk.scrapper.exception.ScrapperException
import io.github.wulkanowy.sdk.scrapper.exception.VulcanException
import io.github.wulkanowy.sdk.scrapper.register.SendCertificateResponse
import io.github.wulkanowy.sdk.scrapper.service.LoginService
import org.slf4j.LoggerFactory
import pl.droidsonroids.jspoon.Jspoon
import java.net.CookieManager
import java.net.URLEncoder
import java.time.LocalDateTime.now
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class LoginHelper(
    var loginType: Scrapper.LoginType,
    private val schema: String,
    private val host: String,
    private val symbol: String,
    private val cookies: CookieManager,
    private val api: LoginService
) {

    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    init {
        logger.debug(toString())
    }

    private val firstStepReturnUrl by lazy {
        encode("$schema://uonetplus.$host/$symbol/LoginEndpoint.aspx").let {
            "/$symbol/FS/LS?wa=wsignin1.0&wtrealm=$it&wctx=$it"
        }
    }

    private val certificateAdapter by lazy {
        Jspoon.create().adapter(CertificateResponse::class.java)
    }

    suspend fun login(email: String, password: String): SendCertificateResponse {
        val res = sendCredentials(email, password)
        logger.info("Login ${loginType.name} started")
        when {
            res.title.startsWith("Witryna ucznia i rodzica") -> return SendCertificateResponse()
            res.action.isBlank() -> throw VulcanException("Invalid certificate page: '${res.title}'. Try again")
        }

        val cert = sendCertificate(res, email)
        logger.debug("Login completed")
        return cert
    }

    suspend fun sendCredentials(email: String, password: String): CertificateResponse {
        email.substringBefore("||").let {
            return when (loginType) {
                AUTO -> throw ScrapperException("You must first specify Api.loginType before logging in")
                STANDARD -> sendStandard(it, password)
                ADFS -> {
                    when (host) {
                        "umt.tarnow.pl" -> {
                            val login = if ("@" in email) email else "EDUNET\\$email"
                            sendADFSMS(login, password)
                        }
                        "edu.gdansk.pl" -> {
                            val login = if ("@" in email) email else "GPE\\$email"
                            sendADFSMS(login, password)
                        }
                        "eduportal.koszalin.pl" -> {
                            val login = if ("@" in email) email else "EDUPORTAL\\$email"
                            sendADFSMS(login, password)
                        }
                        "eszkola.opolskie.pl" -> {
                            val login = if ("@" in email) email else "EDUPORTAL\\$email"
                            sendADFSMS(login, password)
                        }
                        else -> sendADFSMS(it, password)
                    }
                }
                ADFSLight, ADFSLightScoped, ADFSLightCufs -> sendADFSLightGeneric(it, password, loginType)
                ADFSCards -> sendADFSCards(it, password)
            }
        }
    }

    suspend fun sendCertificate(cert: CertificateResponse, email: String, url: String = cert.action): SendCertificateResponse {
        cookies.cookieStore.removeAll()
        val res = api.sendCertificate(
            url = url,
            certificate = mapOf(
                "wa" to cert.wa,
                "wresult" to cert.wresult,
                "wctx" to cert.wctx
            )
        )

        if (email.contains("||")) {
            api.switchLogin("$url?rebuild=${email.substringAfter("||", "")}")
        }

        return res
    }

    private suspend fun sendStandard(email: String, password: String): CertificateResponse {
        return certificateAdapter.fromHtml(
            api.sendCredentials(
                returnUrl = firstStepReturnUrl,
                credentials = mapOf(
                    "LoginName" to email,
                    "Password" to password
                )
            )
        )
    }

    private suspend fun sendADFSLightGeneric(email: String, password: String, type: Scrapper.LoginType): CertificateResponse {
        val res = certificateAdapter.fromHtml(
            api.sendADFSForm(
                url = getADFSUrl(type),
                values = mapOf(
                    "Username" to email,
                    "Password" to password,
                    "x" to "0",
                    "y" to "0"
                )
            )
        )

        logger.debug("Page title after credentials sent: ${res.title}, action: ${res.action} wresult: ${res.wresult.length}, wctx: ${res.wctx}")

        return certificateAdapter.fromHtml(
            api.sendADFSForm(
                url = res.action,
                values = mapOf(
                    "wa" to res.wa,
                    "wresult" to res.wresult,
                    "wctx" to res.wctx
                )
            )
        )
    }

    private suspend fun sendADFSMS(email: String, password: String): CertificateResponse {
        val res = api.sendADFSMSForm(
            url = getADFSUrl(ADFS),
            values = mapOf(
                "UserName" to email,
                "Password" to password,
                "AuthMethod" to "FormsAuthentication"
            )
        )

        val form = certificateAdapter.fromHtml(res)

        return certificateAdapter.fromHtml(
            api.sendADFSForm(
                url = form.action,
                values = mapOf(
                    "wa" to form.wa,
                    "wresult" to form.wresult,
                    "wctx" to form.wctx
                )
            )
        )
    }

    private suspend fun sendADFSCards(email: String, password: String): CertificateResponse {
        val res = api.getForm(getADFSUrl(ADFSCards))

        if (res.formAction.isBlank()) throw VulcanException("Invalid ADFS login page: '${res.title}'. Try again")
        val form = certificateAdapter.fromHtml(
            api.sendADFSForm(
                url = "$schema://adfs.$host/${res.formAction.removePrefix("/")}",
                values = mapOf(
                    "__db" to res.db,
                    "__VIEWSTATE" to res.viewstate,
                    "__VIEWSTATEGENERATOR" to res.viewStateGenerator,
                    "__EVENTVALIDATION" to res.eventValidation,
                    "UsernameTextBox" to email,
                    "PasswordTextBox" to password,
                    "SubmitButton.x" to "0",
                    "SubmitButton.y" to "0"
                )
            )
        )

        return certificateAdapter.fromHtml(
            api.sendADFSForm(
                url = form.action,
                values = mapOf(
                    "wa" to form.wa,
                    "wresult" to form.wresult,
                    "wctx" to form.wctx
                )
            )
        )
    }

    private fun getADFSUrl(type: Scrapper.LoginType): String {
        val id = when (type) {
            ADFS -> if (host == "eduportal.koszalin.pl") "ADFS" else "adfs"
            ADFSCards -> "eSzkola"
            ADFSLightScoped -> "ADFSLight"
            ADFSLightCufs -> "AdfsLight"
            else -> "ADFS"
        }

        val query = "?wa=wsignin1.0" +
            "&wtrealm=" + encode("http${if (ADFSCards != type) "s" else ""}://cufs.$host/$symbol/Account/LogOn") +
            "&wctx=" + encode("rm=0&id=$id&ru=" + encode(firstStepReturnUrl)) +
            "&wct=" + encode(now(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "Z")

        return when (type) {
            ADFSLight -> "$schema://adfslight.$host/LoginPage.aspx?ReturnUrl=" + encode("/$query")
            ADFSLightCufs -> "$schema://logowanie.$host/LoginPage.aspx?ReturnUrl=" + encode("/$query")
            ADFSLightScoped -> "$schema://adfslight.$host/$symbol/LoginPage.aspx?ReturnUrl=" + encode("/$symbol/default.aspx$query")
            else -> "$schema://adfs.$host/adfs/ls/$query"
        }
    }

    private fun encode(url: String) = URLEncoder.encode(url, "UTF-8")
}

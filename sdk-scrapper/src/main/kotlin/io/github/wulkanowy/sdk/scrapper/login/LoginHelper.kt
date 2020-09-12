package io.github.wulkanowy.sdk.scrapper.login

import io.github.wulkanowy.sdk.scrapper.Scrapper
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFS
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFSCards
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFSLight
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFSLightCufs
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFSLightScoped
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.AUTO
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.STANDARD
import io.github.wulkanowy.sdk.scrapper.ScrapperException
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
                ADFS -> sendADFS(it, password)
                ADFSLight, ADFSLightScoped, ADFSLightCufs -> sendADFSLightGeneric(it, password, loginType)
                ADFSCards -> sendADFSCards(it, password)
            }
        }
    }

    suspend fun sendCertificate(cert: CertificateResponse, email: String, url: String = cert.action): SendCertificateResponse {
        cookies.cookieStore.removeAll()
        val res = api.sendCertificate(url, mapOf(
            "wa" to cert.wa,
            "wresult" to cert.wresult,
            "wctx" to cert.wctx
        ))

        if (email.contains("||")) api.switchLogin("$url?rebuild=${email.substringAfter("||", "")}")

        return res
    }

    private suspend fun sendStandard(email: String, password: String): CertificateResponse {
        return certificateAdapter.fromHtml(api.sendCredentials(firstStepReturnUrl, mapOf(
            "LoginName" to email,
            "Password" to password
        )))
    }

    private suspend fun sendADFSLightGeneric(email: String, password: String, type: Scrapper.LoginType): CertificateResponse {
        val res = certificateAdapter.fromHtml(api.sendADFSForm(
            getADFSUrl(type), mapOf(
                "Username" to email,
                "Password" to password,
                "x" to "0",
                "y" to "0"
            )
        ))

        return certificateAdapter.fromHtml(api.sendADFSForm(
            res.action, mapOf(
                "wa" to res.wa,
                "wresult" to res.wresult,
                "wctx" to res.wctx
            )
        ))
    }

    private suspend fun sendADFS(email: String, password: String): CertificateResponse {
        val res = api.getForm(getADFSUrl(ADFS))

        if (res.formAction.isBlank()) throw VulcanException("Invalid ADFS login page: '${res.title}'. Try again")
        val form = certificateAdapter.fromHtml(api.sendADFSForm("$schema://adfs.$host/${res.formAction.removePrefix("/")}", mapOf(
            "__db" to res.db,
            "__VIEWSTATE" to res.viewstate,
            "__VIEWSTATEGENERATOR" to res.viewStateGenerator,
            "__EVENTVALIDATION" to res.eventValidation,
            "UsernameTextBox" to email,
            "PasswordTextBox" to password,
            "SubmitButton.x" to "0",
            "SubmitButton.y" to "0"
        )))

        return certificateAdapter.fromHtml(api.sendADFSForm(form.action, mapOf(
            "wa" to form.wa,
            "wresult" to form.wresult,
            "wctx" to form.wctx
        )))
    }

    private suspend fun sendADFSCards(email: String, password: String): CertificateResponse {
        val form = api.getForm(getADFSUrl(ADFSCards))

        val form2 = api.sendADFSFormStandardChoice("$schema://adfs.$host/${form.formAction.removePrefix("/")}", mapOf(
            "__db" to form.db,
            "__VIEWSTATE" to form.viewstate,
            "__VIEWSTATEGENERATOR" to form.viewStateGenerator,
            "__EVENTVALIDATION" to form.eventValidation,
            "PassiveSignInButton.x" to "0",
            "PassiveSignInButton.y" to "0"
        ))

        val form3 = certificateAdapter.fromHtml(api.sendADFSForm("$schema://adfs.$host/${form2.formAction.removePrefix("/")}", mapOf(
            "__db" to form2.db,
            "__VIEWSTATE" to form2.viewstate,
            "__VIEWSTATEGENERATOR" to form2.viewStateGenerator,
            "__EVENTVALIDATION" to form2.eventValidation,
            "SubmitButton.x" to "0",
            "SubmitButton.y" to "0",
            "UsernameTextBox" to email,
            "PasswordTextBox" to password
        )))

        return certificateAdapter.fromHtml(api.sendADFSForm(form3.action, mapOf(
            "wa" to form3.wa,
            "wresult" to form3.wresult,
            "wctx" to form3.wctx
        )))
    }

    private fun getADFSUrl(type: Scrapper.LoginType): String {
        val id = when (type) {
            ADFS -> "adfs"
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

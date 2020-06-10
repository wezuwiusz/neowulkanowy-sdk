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
import io.reactivex.Single
import org.slf4j.LoggerFactory
import org.threeten.bp.LocalDateTime.now
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import pl.droidsonroids.jspoon.Jspoon
import java.net.CookieManager
import java.net.URLEncoder

class LoginHelper(
    var loginType: Scrapper.LoginType,
    private val schema: String,
    private val host: String,
    private val symbol: String,
    private val cookies: CookieManager,
    private val api: LoginService
) {

    companion object {
        @JvmStatic private val logger = LoggerFactory.getLogger(this::class.java)
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

    @Synchronized
    fun login(email: String, password: String): Single<SendCertificateResponse> {
        return sendCredentials(email, password).flatMap {
            logger.info("Login started")
            when {
                it.title.startsWith("Witryna ucznia i rodzica") -> return@flatMap Single.just(SendCertificateResponse())
                it.action.isBlank() -> throw VulcanException("Invalid certificate page: '${it.title}'. Try again")
            }

            sendCertificate(it, email)
        }.map {
            logger.debug("Login completed")
            it
        }
    }

    fun sendCredentials(email: String, password: String): Single<CertificateResponse> {
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

    fun sendCertificate(cert: CertificateResponse, email: String, url: String = cert.action): Single<SendCertificateResponse> {
        cookies.cookieStore.removeAll()
        return api.sendCertificate(url, mapOf(
            "wa" to cert.wa,
            "wresult" to cert.wresult,
            "wctx" to cert.wctx
        )).flatMap {
            if (email.contains("||")) api.switchLogin("$url?rebuild=${email.substringAfter("||", "")}")
            else Single.just(it)
        }
    }

    private fun sendStandard(email: String, password: String): Single<CertificateResponse> {
        return api.sendCredentials(firstStepReturnUrl, mapOf(
            "LoginName" to email,
            "Password" to password
        )).map { certificateAdapter.fromHtml(it) }
    }

    private fun sendADFSLightGeneric(email: String, password: String, type: Scrapper.LoginType): Single<CertificateResponse> {
        return api.sendADFSForm(
            getADFSUrl(type), mapOf(
                "Username" to email,
                "Password" to password,
                "x" to "0",
                "y" to "0"
            )
        ).map { certificateAdapter.fromHtml(it) }.flatMap {
            api.sendADFSForm(
                it.action, mapOf(
                    "wa" to it.wa,
                    "wresult" to it.wresult,
                    "wctx" to it.wctx
                )
            )
        }.map { certificateAdapter.fromHtml(it) }
    }

    private fun sendADFS(email: String, password: String): Single<CertificateResponse> {
        return api.getForm(getADFSUrl(ADFS)).flatMap {
            if (it.formAction.isBlank()) throw VulcanException("Invalid ADFS login page: '${it.title}'. Try again")
            api.sendADFSForm("$schema://adfs.$host/${it.formAction.removePrefix("/")}", mapOf(
                "__db" to it.db,
                "__VIEWSTATE" to it.viewstate,
                "__VIEWSTATEGENERATOR" to it.viewStateGenerator,
                "__EVENTVALIDATION" to it.eventValidation,
                "UsernameTextBox" to email,
                "PasswordTextBox" to password,
                "SubmitButton.x" to "0",
                "SubmitButton.y" to "0"
            ))
        }.map { certificateAdapter.fromHtml(it) }.flatMap {
            api.sendADFSForm(it.action, mapOf(
                "wa" to it.wa,
                "wresult" to it.wresult,
                "wctx" to it.wctx
            ))
        }.map { certificateAdapter.fromHtml(it) }
    }

    private fun sendADFSCards(email: String, password: String): Single<CertificateResponse> {
        return api.getForm(getADFSUrl(ADFSCards)).flatMap {
            api.sendADFSFormStandardChoice("$schema://adfs.$host/${it.formAction.removePrefix("/")}", mapOf(
                "__db" to it.db,
                "__VIEWSTATE" to it.viewstate,
                "__VIEWSTATEGENERATOR" to it.viewStateGenerator,
                "__EVENTVALIDATION" to it.eventValidation,
                "PassiveSignInButton.x" to "0",
                "PassiveSignInButton.y" to "0"
            ))
        }.flatMap {
            api.sendADFSForm("$schema://adfs.$host/${it.formAction.removePrefix("/")}", mapOf(
                "__db" to it.db,
                "__VIEWSTATE" to it.viewstate,
                "__VIEWSTATEGENERATOR" to it.viewStateGenerator,
                "__EVENTVALIDATION" to it.eventValidation,
                "SubmitButton.x" to "0",
                "SubmitButton.y" to "0",
                "UsernameTextBox" to email,
                "PasswordTextBox" to password
            ))
        }.map { certificateAdapter.fromHtml(it) }.flatMap {
            api.sendADFSForm(it.action, mapOf(
                "wa" to it.wa,
                "wresult" to it.wresult,
                "wctx" to it.wctx
            ))
        }.map { certificateAdapter.fromHtml(it) }
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

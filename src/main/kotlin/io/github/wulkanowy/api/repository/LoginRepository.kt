package io.github.wulkanowy.api.repository

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.api.ApiException
import io.github.wulkanowy.api.interceptor.VulcanException
import io.github.wulkanowy.api.login.CertificateResponse
import io.github.wulkanowy.api.register.HomepageResponse
import io.github.wulkanowy.api.service.LoginService
import io.reactivex.Single
import org.threeten.bp.LocalDateTime.now
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import pl.droidsonroids.jspoon.Jspoon
import java.net.CookieManager
import java.net.URLEncoder

class LoginRepository(
        var loginType: Api.LoginType,
        private val schema: String,
        private val host: String,
        private val symbol: String,
        private val cookies: CookieManager,
        private val api: LoginService
) {

    private val firstStepReturnUrl by lazy {
        encode("$schema://uonetplus.$host/$symbol/LoginEndpoint.aspx").let {
            "/$symbol/FS/LS?wa=wsignin1.0&wtrealm=$it&wctx=$it"
        }
    }

    private val certificateAdapter by lazy {
        Jspoon.create().adapter(CertificateResponse::class.java)
    }

    @Synchronized
    fun login(email: String, password: String): Single<HomepageResponse> {
        return sendCredentials(email, password).flatMap {
            when {
                it.title.startsWith("Witryna ucznia i rodzica") -> return@flatMap Single.just(HomepageResponse())
                it.action.isBlank() -> throw VulcanException("Invalid certificate page: '${it.title}'. Try again")
            }

            sendCertificate(it)
        }
    }

    fun sendCredentials(email: String, password: String): Single<CertificateResponse> {
        return when (loginType) {
            Api.LoginType.AUTO -> throw ApiException("You must first specify LoginType before logging in")
            Api.LoginType.STANDARD -> sendStandard(email, password)
            Api.LoginType.ADFS -> sendAdfs(email, password)
            Api.LoginType.ADFSLight -> sendADFSLight(email, password)
            Api.LoginType.ADFSCards -> sendADFSCards(email, password)
        }
    }

    fun sendCertificate(certificate: CertificateResponse, url: String = certificate.action): Single<HomepageResponse> {
        cookies.cookieStore.removeAll()
        return api.sendCertificate(url, mapOf(
                "wa" to certificate.wa,
                "wresult" to certificate.wresult,
                "wctx" to certificate.wctx
        ))
    }

    private fun sendStandard(email: String, password: String): Single<CertificateResponse> {
        return api.sendCredentials(firstStepReturnUrl, mapOf(
                "LoginName" to email,
                "Password" to password
        )).map { certificateAdapter.fromHtml(it) }
    }

    private fun sendADFSLight(email: String, password: String): Single<CertificateResponse> {
        return api.sendADFSForm(getADFSUrl(Api.LoginType.ADFSLight), mapOf(
                "Username" to email,
                "Password" to password,
                "x" to "0",
                "y" to "0"
        )).map { certificateAdapter.fromHtml(it) }.flatMap {
            api.sendADFSForm(it.action, mapOf(
                    "wa" to it.wa,
                    "wresult" to it.wresult,
                    "wctx" to it.wctx
            ))
        }.map { certificateAdapter.fromHtml(it) }
    }

    private fun sendAdfs(email: String, password: String): Single<CertificateResponse> {
        return api.getForm(getADFSUrl(Api.LoginType.ADFS)).flatMap {
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
        }.map { certificateAdapter.fromHtml(it) }.flatMap {
            api.sendADFSForm(it.action, mapOf(
                    "wa" to it.wa,
                    "wresult" to it.wresult,
                    "wctx" to it.wctx
            ))
        }.map { certificateAdapter.fromHtml(it) }
    }

    private fun sendADFSCards(email: String, password: String): Single<CertificateResponse> {
        return api.getForm(getADFSUrl(Api.LoginType.ADFSCards)).flatMap {
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
        }.map { certificateAdapter.fromHtml(it) }.flatMap {
            api.sendADFSForm(it.action, mapOf(
                    "wa" to it.wa,
                    "wresult" to it.wresult,
                    "wctx" to it.wctx
            ))
        }.map { certificateAdapter.fromHtml(it) }
    }

    private fun getADFSUrl(type: Api.LoginType): String {
        val id = when (type) {
            Api.LoginType.ADFS -> "adfs"
            Api.LoginType.ADFSCards -> "eSzkola"
            else -> "ADFS"
        }

        val hostPart = when (type) {
            Api.LoginType.ADFSLight -> "$schema://adfslight.$host/LoginPage.aspx?ReturnUrl=" + encode("/")
            else -> "$schema://adfs.$host/adfs/ls/"
        }

        return hostPart +
                "?wa=wsignin1.0" +
                "&wtrealm=" + encode("http${if (Api.LoginType.ADFSCards != type) "s" else ""}://cufs.$host/$symbol/Account/LogOn") +
                "&wctx=" + encode("rm=0&id=$id&ru=" + encode(firstStepReturnUrl)) +
                "&wct=" + encode(now(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "Z")
    }

    private fun encode(url: String) = URLEncoder.encode(url, "UTF-8")
}

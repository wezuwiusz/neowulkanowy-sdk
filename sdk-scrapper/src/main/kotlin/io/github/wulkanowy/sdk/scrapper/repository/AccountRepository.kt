package io.github.wulkanowy.sdk.scrapper.repository

import io.github.wulkanowy.sdk.scrapper.Scrapper
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFS
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFSCards
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFSLight
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFSLightCufs
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFSLightScoped
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.AUTO
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.STANDARD
import io.github.wulkanowy.sdk.scrapper.exception.ScrapperException
import io.github.wulkanowy.sdk.scrapper.exception.InvalidCaptchaException
import io.github.wulkanowy.sdk.scrapper.exception.InvalidEmailException
import io.github.wulkanowy.sdk.scrapper.exception.NoAccountFoundException
import io.github.wulkanowy.sdk.scrapper.exception.PasswordResetErrorException
import io.github.wulkanowy.sdk.scrapper.service.AccountService
import io.github.wulkanowy.sdk.scrapper.service.ServiceManager
import java.net.URL

class AccountRepository(private val account: AccountService) {

    companion object {
        const val SELECTOR_STANDARD = ".loginButton, .LogOnBoard input[type=submit]" // remove second selector?
        const val SELECTOR_ADFS = "#loginArea form#loginForm"
        const val SELECTOR_ADFS_LIGHT = ".submit-button"
        const val SELECTOR_ADFS_CARDS = "#__VIEWSTATE"
    }

    suspend fun getPasswordResetCaptcha(registerBaseUrl: String, symbol: String): Pair<String, String> {
        val (_, resetUrl) = getPasswordResetUrl(registerBaseUrl, symbol.trim())
        val res = account.getPasswordResetPageWithCaptcha(resetUrl)
        return resetUrl to res.recaptchaSiteKey
    }

    suspend fun sendPasswordResetRequest(registerBaseUrl: String, symbol: String, email: String, captchaCode: String): String {
        val (type, url) = getPasswordResetUrl(registerBaseUrl, symbol.trim())

        val res = when (type) {
            STANDARD -> account.sendPasswordResetRequest(url, email, captchaCode)
            ADFSLight, ADFSLightScoped, ADFSLightCufs -> account.sendPasswordResetRequestADFSLight(url, email, captchaCode)
            ADFS, ADFSCards -> {
                val page = account.getPasswordResetPageADFS(url)
                val formFields = page.html.select("[type=hidden]").associate { input ->
                    input.attr("name") to input.attr("value")
                }
                account.sendPasswordResetRequestADFS(
                    url = url,
                    username = email,
                    captchaCode = captchaCode,
                    viewStateParams = formFields.plus("btSend.x" to "5").plus("btSend.y" to "6")
                )
            }
            else -> throw ScrapperException("Never happen")
        }

        with(res.html) {
            select(".ErrorMessage").text().let { // STANDARD
                if (it.contains("Niepoprawny adres email")) throw InvalidEmailException(it)
            }
            select(".ErrorMessage, #ErrorTextLabel, #lblStatus").text()?.let { // STANDARD, ADFSLight, ADFSCards
                if (it.contains("nie zostało odnalezione lub zostało zablokowane")) throw NoAccountFoundException(it)
                if (it.contains("nie ma w systemie zarejestrowanych")) throw NoAccountFoundException(it) // 😀
                if (it.contains("żądanie nie zostało poprawnie autoryzowane")) throw InvalidCaptchaException(it)
            }
        }
        if (!res.message.startsWith("Wysłano wiadomość")) throw PasswordResetErrorException("Unexpected message: ${res.message}")

        return res.message
    }

    private suspend fun getPasswordResetUrl(registerBaseUrl: String, symbol: String): Pair<Scrapper.LoginType, String> {
        val url = URL(registerBaseUrl)
        val unlockUrl = when (url.host) {
            "fakelog.cf" -> STANDARD to "https://cufs.fakelog.cf/Default/AccountManage/UnlockAccount"
            "fakelog.tk" -> STANDARD to "https://cufs.fakelog.tk/Default/AccountManage/UnlockAccount"
            "eszkola.opolskie.pl" -> ADFSCards to "https://konta.eszkola.opolskie.pl/maintenance/unlock.aspx"
            "edu.gdansk.pl" -> ADFS to "https://konta.edu.gdansk.pl/maintenance/unlock.aspx"
            "edu.lublin.eu" -> ADFSLightCufs to "https://logowanie.edu.lublin.eu/AccountManage/UnlockAccountRequest"
            "resman.pl" -> ADFSLight to "https://adfslight.resman.pl/AccountManage/UnlockAccountRequest"
            "umt.tarnow.pl" -> ADFS to "https://konta.umt.tarnow.pl/maintenance/unlock.aspx"
            "eduportal.koszalin.pl" -> ADFS to "https://konta.eduportal.koszalin.pl/maintenance/unlock.aspx"
            "vulcan.net.pl" -> AUTO to "" // stream hack - bellow
            else -> throw ScrapperException("Nieznany dziennik $url")
        }

        return if (unlockUrl.first == AUTO) {
            val loginType = getLoginType(ServiceManager.UrlGenerator(url, symbol, ""))
            loginType to when (loginType) {
                STANDARD -> "https://cufs.vulcan.net.pl/$symbol/AccountManage/UnlockAccount"
                ADFSLightScoped -> "https://adfslight.vulcan.net.pl/$symbol/AccountManage/UnlockAccountRequest"
                else -> throw ScrapperException("Nieznany dziennik $registerBaseUrl, $loginType")
            }
        } else unlockUrl
    }

    private suspend fun getLoginType(urlGenerator: ServiceManager.UrlGenerator): Scrapper.LoginType {
        val page = account.getFormType(urlGenerator.generate(ServiceManager.UrlGenerator.Site.LOGIN) + "Account/LogOn").page

        return when {
            page.select(SELECTOR_STANDARD).isNotEmpty() -> STANDARD
            page.select(SELECTOR_ADFS).isNotEmpty() -> ADFS
            page.select(SELECTOR_ADFS_LIGHT).isNotEmpty() -> {
                page.selectFirst("form")?.attr("action").orEmpty().run {
                    when {
                        contains("cufs.edu.lublin.eu") -> ADFSLightCufs
                        startsWith("/LoginPage.aspx") -> ADFSLight
                        startsWith("/${urlGenerator.symbol}/LoginPage.aspx") -> ADFSLightScoped
                        else -> throw ScrapperException("Nieznany typ dziennika ADFS")
                    }
                }
            }
            page.select(SELECTOR_ADFS_CARDS).isNotEmpty() -> ADFSCards
            else -> throw ScrapperException("Nieznany typ dziennika '${page.select("title")}")
        }
    }
}

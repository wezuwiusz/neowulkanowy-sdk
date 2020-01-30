package io.github.wulkanowy.sdk.scrapper.repository

import io.github.wulkanowy.sdk.scrapper.Scrapper
import io.github.wulkanowy.sdk.scrapper.ScrapperException
import io.github.wulkanowy.sdk.scrapper.service.AccountService
import io.github.wulkanowy.sdk.scrapper.service.ServiceManager
import io.reactivex.Single
import java.net.URL

class AccountRepository(private val account: AccountService) {

    fun getPasswordResetCaptcha(registerBaseUrl: String, symbol: String): Single<Pair<String, String>> {
        return getPasswordResetUrl(registerBaseUrl, symbol)
            .flatMap { (_, resetUrl) ->
                account.getPasswordResetPageWithCaptcha(resetUrl)
                    .map { res -> resetUrl to res.recaptchaSiteKey }
            }
    }

    fun sendPasswordResetRequest(registerBaseUrl: String, symbol: String, email: String, captchaCode: String): Single<Pair<Boolean, String>> {
        return getPasswordResetUrl(registerBaseUrl, symbol)
            .flatMap { account.sendPasswordResetRequest(it.second, email, captchaCode) }
            .map {
                (it.title == "Podsumowanie operacji") to it.message
            }
    }

    private fun getPasswordResetUrl(registerBaseUrl: String, symbol: String): Single<Pair<Scrapper.LoginType, String>> {
        val url = URL(registerBaseUrl)
        return when (url.host) {
            "fakelog.cf" -> Single.just(Scrapper.LoginType.STANDARD to "https://cufs.fakelog.cf/Default/AccountManage/UnlockAccount")
            "eszkola.opolskie.pl" -> Single.just(Scrapper.LoginType.ADFSCards to "https://konta.eszkola.opolskie.pl/maintenance/unlock.aspx")
            "edu.gdansk.pl" -> Single.just(Scrapper.LoginType.ADFSLight to "https://konta.edu.gdansk.pl/maintenance/unlock.aspx")
            "edu.lublin.eu" -> Single.just(Scrapper.LoginType.ADFSLightCufs to "https://logowanie.edu.lublin.eu/AccountManage/UnlockAccountRequest")
            "resman.pl" -> Single.just(Scrapper.LoginType.ADFSLight to "https://adfslight.resman.pl/AccountManage/UnlockAccountRequest")
            "vulcan.net.pl" -> getLoginType(ServiceManager.UrlGenerator(url, symbol, "")).map {
                it to when (it) {
                    Scrapper.LoginType.STANDARD -> "https://cufs.vulcan.net.pl/Default/AccountManage/UnlockAccount"
                    Scrapper.LoginType.ADFSLightScoped -> "https://adfslight.vulcan.net.pl/$symbol/AccountManage/UnlockAccountRequest"
                    else -> throw ScrapperException("Nieznany dziennik $it")
                }
            }
            else -> throw ScrapperException("Nieznany dziennik $url")
        }
    }

    private fun getLoginType(urlGenerator: ServiceManager.UrlGenerator): Single<Scrapper.LoginType> {
        return account.getFormType(urlGenerator.generate(ServiceManager.UrlGenerator.Site.LOGIN) + "Account/LogOn").map { it.page }.map {
            when {
                it.select(".LogOnBoard input[type=submit]").isNotEmpty() -> Scrapper.LoginType.STANDARD
                it.select("form[name=form1] #SubmitButton").isNotEmpty() -> Scrapper.LoginType.ADFS
                it.select(".submit-button, form #SubmitButton").isNotEmpty() -> {
                    it.selectFirst("form").attr("action").run {
                        when {
                            contains("cufs.edu.lublin.eu") -> Scrapper.LoginType.ADFSLightCufs
                            startsWith("/LoginPage.aspx") -> Scrapper.LoginType.ADFSLight
                            startsWith("/${urlGenerator.symbol}/LoginPage.aspx") -> Scrapper.LoginType.ADFSLightScoped
                            else -> throw ScrapperException("Nieznany typ dziennika ADFS")
                        }
                    }
                }
                it.select("#PassiveSignInButton").isNotEmpty() -> Scrapper.LoginType.ADFSCards
                else -> throw ScrapperException("Nieznany typ dziennika '${it.select("title")}")
            }
        }
    }
}

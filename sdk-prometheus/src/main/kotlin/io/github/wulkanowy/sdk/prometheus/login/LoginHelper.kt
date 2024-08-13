package io.github.wulkanowy.sdk.prometheus.login

import io.github.wulkanowy.sdk.prometheus.CookieJarCabinet
import io.github.wulkanowy.sdk.prometheus.exception.CaptchaException
import io.github.wulkanowy.sdk.prometheus.service.LoginService
import org.jsoup.Jsoup

internal class LoginHelper(
    private val cookieJarCabinet: CookieJarCabinet,
    private val api: LoginService,
) {
    suspend fun login(username: String, password: String) {
        cookieJarCabinet.beforeUserLogIn()
        val loginPageHTML = api.getLoginPage()
        val csrfToken = Jsoup.parse(loginPageHTML).select("input[name=\"__RequestVerificationToken\"]").attr("value")

        val queryUserInfoResponse = api.queryUserInfo(
            mapOf(
                "alias" to username,
            ),
        )
        if (queryUserInfoResponse.data?.showCaptcha == false) {
            api.logIn(
                mapOf(
                    "Alias" to username,
                    "Password" to password,
                    "captchaUser" to "",
                    "__RequestVerificationToken" to csrfToken,
                ),
            )
        } else {
            // TODO: Pass captchas to the user
            throw CaptchaException("Captcha required to authenticate.")
        }
    }
}

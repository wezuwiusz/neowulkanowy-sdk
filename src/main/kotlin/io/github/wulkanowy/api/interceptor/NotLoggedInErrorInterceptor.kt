package io.github.wulkanowy.api.interceptor

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.api.login.NotLoggedInException
import okhttp3.Interceptor
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.select.Elements

class NotLoggedInErrorInterceptor(private val loginType: Api.LoginType) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val doc = Jsoup.parse(response.peekBody(Long.MAX_VALUE).string())

        if (when (loginType) {
                    Api.LoginType.STANDARD -> doc.select(".loginButton, .LogOnBoard input[type=submit]")
                    Api.LoginType.ADFS -> doc.select("form[name=form1] #SubmitButton")
                    Api.LoginType.ADFSLight -> doc.select("form #SubmitButton")
                    Api.LoginType.ADFSCards -> doc.select("#PassiveSignInButton")
                    else -> Elements()
                }.isNotEmpty()) {
            throw NotLoggedInException("User not logged in")
        }

        doc.body().text().let {
            // /messages
            if (it.contains("The custom error module")) throw NotLoggedInException("Zaloguj się")
        }

        return response
    }
}

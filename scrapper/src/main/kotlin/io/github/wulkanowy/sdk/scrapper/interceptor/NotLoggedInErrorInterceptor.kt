package io.github.wulkanowy.sdk.scrapper.interceptor

import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFS
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFSCards
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFSLight
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFSLightScoped
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.STANDARD
import io.github.wulkanowy.sdk.scrapper.login.NotLoggedInException
import okhttp3.Interceptor
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.select.Elements

class NotLoggedInErrorInterceptor(private val loginType: LoginType) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val doc = Jsoup.parse(response.peekBody(Long.MAX_VALUE).string())

        // if (chain.request().url().toString().contains("/Start.mvc/Get")) {
        if (chain.request().url().toString().contains("/Start.mvc/")) { // /Index return error too in 19.09.0000.34977
            doc.select(".errorBlock").let {
                if (it.isNotEmpty()) throw NotLoggedInException(it.select(".errorTitle").text())
            }
        }

        if (when (loginType) {
                STANDARD -> doc.select(".loginButton, .LogOnBoard input[type=submit]")
                ADFS -> doc.select("form[name=form1] #SubmitButton")
                ADFSLight, ADFSLightScoped -> doc.select("form #SubmitButton")
                ADFSCards -> doc.select("#PassiveSignInButton")
                else -> Elements()
            }.isNotEmpty()
        ) {
            throw NotLoggedInException("User not logged in")
        }

        doc.body().text().let {
            // /messages
            if (it.contains("The custom error module")) throw NotLoggedInException("Zaloguj się")
        }

        return response
    }
}

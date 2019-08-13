package io.github.wulkanowy.api.interceptor

import io.github.wulkanowy.api.Api.LoginType
import io.github.wulkanowy.api.Api.LoginType.ADFS
import io.github.wulkanowy.api.Api.LoginType.ADFSCards
import io.github.wulkanowy.api.Api.LoginType.ADFSLight
import io.github.wulkanowy.api.Api.LoginType.ADFSLightScoped
import io.github.wulkanowy.api.Api.LoginType.STANDARD
import io.github.wulkanowy.api.login.NotLoggedInException
import okhttp3.Interceptor
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.select.Elements

class NotLoggedInErrorInterceptor(private val loginType: LoginType) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val doc = Jsoup.parse(response.peekBody(Long.MAX_VALUE).string())

        if (chain.request().url().toString().contains("/Start.mvc/")) {
            doc.select(".errorBlock").let {
                if (it.isNotEmpty()) throw NotLoggedInException("${it.select(".errorTitle").text()}}")
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

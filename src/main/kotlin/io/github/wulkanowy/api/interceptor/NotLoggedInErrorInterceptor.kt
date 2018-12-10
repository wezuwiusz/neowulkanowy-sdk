package io.github.wulkanowy.api.interceptor

import io.github.wulkanowy.api.login.NotLoggedInException
import okhttp3.Interceptor
import okhttp3.Response
import org.jsoup.Jsoup

class NotLoggedInErrorInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val doc = Jsoup.parse(response.peekBody(Long.MAX_VALUE).string())

        doc.select(".loginButton, .LogOnBoard input[type=submit], #PassiveSignInButton, form #SubmitButton, form[name=form1] #SubmitButton").let {
            if (it.isNotEmpty()) throw NotLoggedInException("User not logged in")
        }

        doc.body().text().let { // /messages
            if (it.contains("The custom error module")) throw NotLoggedInException("User not logged in")
        }

        return response
    }
}

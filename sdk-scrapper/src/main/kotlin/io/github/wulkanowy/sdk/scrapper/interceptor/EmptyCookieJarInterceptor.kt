package io.github.wulkanowy.sdk.scrapper.interceptor

import io.github.wulkanowy.sdk.scrapper.login.NotLoggedInException
import okhttp3.Interceptor
import okhttp3.Response
import java.net.CookieManager

class EmptyCookieJarInterceptor(private val jar: CookieManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (jar.cookieStore.cookies.isEmpty()) {
            throw NotLoggedInException("No cookie found! You are not logged in yet")
        }

        return chain.proceed(chain.request())
    }
}

package io.github.wulkanowy.api

import okhttp3.Interceptor
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import java.net.CookieManager

class ClientCreator(cookies: CookieManager) {

    private val builder = OkHttpClient().newBuilder().cookieJar(JavaNetCookieJar(cookies))

    fun addInterceptor(interceptor: Interceptor): ClientCreator {
        builder.addInterceptor(interceptor)
        return this
    }

    fun getClient(): OkHttpClient {
        return builder.build()
    }
}

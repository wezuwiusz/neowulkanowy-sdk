package io.github.wulkanowy.sdk.mobile.interceptor

import io.github.wulkanowy.sdk.mobile.exception.InvalidSymbolException
import okhttp3.Interceptor
import okhttp3.Response

class ErrorInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        when (response.peekBody(Long.MAX_VALUE).string()) {
            "Bad Request" -> throw InvalidSymbolException()
        }

        return response
    }
}

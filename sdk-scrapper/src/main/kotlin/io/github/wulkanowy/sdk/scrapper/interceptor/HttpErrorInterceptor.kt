package io.github.wulkanowy.sdk.scrapper.interceptor

import io.github.wulkanowy.sdk.scrapper.exception.ScrapperException
import io.github.wulkanowy.sdk.scrapper.exception.ServiceUnavailableException
import io.github.wulkanowy.sdk.scrapper.exception.VulcanException
import io.github.wulkanowy.sdk.scrapper.login.NotLoggedInException
import okhttp3.Interceptor
import okhttp3.Response

class HttpErrorInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        if (response.isSuccessful || response.isRedirect) return response

        return when (response.code()) {
            429 -> throw NotLoggedInException(response.body()?.string() ?: response.message())
            404 -> throw ScrapperException(response.code().toString() + ": " + response.message())
            in 400..499 -> throw VulcanException(response.code().toString() + ": " + response.message())
            in 500..599 -> throw ServiceUnavailableException(response.code().toString() + ": " + response.message())
            else -> response
        }
    }
}

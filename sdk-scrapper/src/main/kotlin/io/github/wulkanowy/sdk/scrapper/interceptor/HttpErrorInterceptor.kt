package io.github.wulkanowy.sdk.scrapper.interceptor

import io.github.wulkanowy.sdk.scrapper.exception.ScrapperException
import io.github.wulkanowy.sdk.scrapper.exception.ServiceUnavailableException
import io.github.wulkanowy.sdk.scrapper.exception.VulcanClientError
import io.github.wulkanowy.sdk.scrapper.login.NotLoggedInException
import okhttp3.Interceptor
import okhttp3.Response

internal class HttpErrorInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        if (response.isSuccessful || response.isRedirect) return response

        val message = if (response.body?.contentType()?.subtype == "json") {
            response.body?.string().orEmpty()
        } else response.message

        return when (response.code) {
            429 -> throw NotLoggedInException(message)
            404 -> throw ScrapperException(response.code.toString() + ": " + message, response.code)
            in 400..402 -> throw VulcanClientError(response.code.toString() + ": " + message, response.code)
            403 -> {
                if (response.body?.contentType()?.subtype == "json") {
                    throw VulcanClientError(response.code.toString() + ": " + message, response.code)
                }
                response
            }

            in 405..499 -> throw VulcanClientError(response.code.toString() + ": " + message, response.code)
            in 500..599 -> throw ServiceUnavailableException(response.code.toString() + ": " + message)
            else -> response
        }
    }
}

package io.github.wulkanowy.sdk.scrapper.interceptor

import io.github.wulkanowy.sdk.scrapper.ApiResponse
import io.github.wulkanowy.sdk.scrapper.exception.AuthorizationRequiredException
import io.github.wulkanowy.sdk.scrapper.exception.FeatureDisabledException
import io.github.wulkanowy.sdk.scrapper.exception.InvalidPathException
import io.github.wulkanowy.sdk.scrapper.exception.MissingCsrfException
import io.github.wulkanowy.sdk.scrapper.exception.ScrapperException
import io.github.wulkanowy.sdk.scrapper.exception.VulcanException

internal fun <T> ApiResponse<T>.handleErrors(): ApiResponse<T> {
    return when {
        !success && feedback != null -> throw feedback.run {
            when {
                "niespójność danych" in message -> ScrapperException(message)
                "Brak uprawnień" in message -> AuthorizationRequiredException(message)
                "wyłączony" in message -> FeatureDisabledException(message)
                "DB_ERROR" in message -> VulcanException(message)
                "błąd" in message -> VulcanException(message)
                "The controller for path" in message -> InvalidPathException(message)
                "The parameters dictionary contains a null entry for parameter" in message -> InvalidPathException(message)
                "Brak wymaganego ciastka anti-forgery" in message -> MissingCsrfException(message)
                else -> VulcanException(message)
            }
        }

        else -> this
    }
}

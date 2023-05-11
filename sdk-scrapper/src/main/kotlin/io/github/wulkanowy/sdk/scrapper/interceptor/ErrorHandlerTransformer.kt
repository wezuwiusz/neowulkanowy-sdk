package io.github.wulkanowy.sdk.scrapper.interceptor

import io.github.wulkanowy.sdk.scrapper.ApiResponse
import io.github.wulkanowy.sdk.scrapper.exception.AuthorizationRequiredException
import io.github.wulkanowy.sdk.scrapper.exception.FeatureDisabledException
import io.github.wulkanowy.sdk.scrapper.exception.InvalidPathException
import io.github.wulkanowy.sdk.scrapper.exception.ScrapperException
import io.github.wulkanowy.sdk.scrapper.exception.VulcanException

internal fun <T> ApiResponse<T>.handleErrors(): ApiResponse<T> {
    return if (!success && feedback != null) throw feedback.run {
        when {
            message.contains("niespójność danych") -> ScrapperException(message)
            message.contains("Brak uprawnień") -> AuthorizationRequiredException(message)
            message.contains("wyłączony") -> FeatureDisabledException(message)
            message.contains("DB_ERROR") -> VulcanException(message)
            message.contains("błąd") -> VulcanException(message)
            message.contains("The controller for path") -> InvalidPathException(message)
            message.contains("The parameters dictionary contains a null entry for parameter") -> InvalidPathException(message)
            else -> VulcanException(message)
        }
    } else this
}

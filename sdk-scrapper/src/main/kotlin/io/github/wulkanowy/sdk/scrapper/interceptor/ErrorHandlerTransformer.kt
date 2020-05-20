package io.github.wulkanowy.sdk.scrapper.interceptor

import io.github.wulkanowy.sdk.scrapper.ApiResponse
import io.github.wulkanowy.sdk.scrapper.ScrapperException
import io.github.wulkanowy.sdk.scrapper.exception.InvalidPathException
import io.github.wulkanowy.sdk.scrapper.login.AccountPermissionException
import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.SingleTransformer

class ErrorHandlerTransformer<T : Any?> : SingleTransformer<ApiResponse<T>, ApiResponse<T>> {

    override fun apply(upstream: Single<ApiResponse<T>>): SingleSource<ApiResponse<T>> {
        return upstream.map { res ->
            if (!res.success) throw res.feedback.run {
                when {
                    message.contains("niespójność danych") -> ScrapperException(message)
                    message.contains("Brak uprawnień") -> AccountPermissionException(message)
                    message.contains("wyłączony") -> FeatureDisabledException(message)
                    message.contains("DB_ERROR") -> VulcanException(message)
                    message.contains("błąd") -> VulcanException(message)
                    message.contains("The controller for path") -> InvalidPathException(message)
                    message.contains("The parameters dictionary contains a null entry for parameter") -> InvalidPathException(message)
                    else -> VulcanException(message)
                }
            }
            else res
        }
    }
}

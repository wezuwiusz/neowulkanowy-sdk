package io.github.wulkanowy.api.interceptor

import io.github.wulkanowy.api.ApiException
import io.github.wulkanowy.api.ApiResponse
import io.github.wulkanowy.api.login.AccountPermissionException
import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.SingleTransformer

class ErrorHandlerTransformer<T : Any?> : SingleTransformer<ApiResponse<T>, ApiResponse<T>> {

    override fun apply(upstream: Single<ApiResponse<T>>): SingleSource<ApiResponse<T>> {
        return upstream.flatMap { res ->
            if (!res.success) throw res.feedback.run {
                when {
                    message.contains("niespójność danych") -> ApiException(message)
                    message.contains("Brak uprawnień") -> AccountPermissionException(message)
                    message.contains("wyłączony") -> FeatureDisabledException(message)
                    message.contains("DB_ERROR") -> VulcanException(message)
                    message.contains("błąd") -> VulcanException(message)
                    else -> VulcanException(message)
                }
            }
            else Single.just(res)
        }
    }
}

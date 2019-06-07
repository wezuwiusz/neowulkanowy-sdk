package io.github.wulkanowy.api.interceptor

import io.github.wulkanowy.api.ApiResponse
import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.SingleTransformer

class ErrorHandlerTransformer<T : Any?> : SingleTransformer<ApiResponse<T>, ApiResponse<T>> {

    override fun apply(upstream: Single<ApiResponse<T>>): SingleSource<ApiResponse<T>> {
        return upstream.flatMap { res ->
            if (!res.success) throw res.feedback.run {
                when {
                    message.contains("wyłączony") -> FeatureDisabledException(message)
                    else -> VulcanException(message)
                }
            }
            else Single.just(res)
        }
    }
}

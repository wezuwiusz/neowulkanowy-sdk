package io.github.wulkanowy.api.interceptor

import io.github.wulkanowy.api.ApiResponse
import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.SingleTransformer

class ErrorHandlerTransformer<T : Any> : SingleTransformer<ApiResponse<T>, T> {

    override fun apply(upstream: Single<ApiResponse<T>>): SingleSource<T> {
        return upstream.flatMap { res ->
            if (!res.success) res.feedback.run {
                when {
                    message.contains("wyłączony") -> throw FeatureDisabledException(message)
                    else -> throw VulcanException(message)
                }
            } else Single.just(res.data)
        }
    }
}

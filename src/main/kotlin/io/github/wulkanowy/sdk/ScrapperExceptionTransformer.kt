package io.github.wulkanowy.sdk

import io.github.wulkanowy.api.login.BadCredentialsException as ScrapperBadCredentialsException
import io.github.wulkanowy.sdk.exception.*
import io.github.wulkanowy.api.interceptor.ServiceUnavailableException as ScrapperServiceUnavailableException
import io.github.wulkanowy.api.interceptor.FeatureDisabledException as ScrapperFeatureDisabledException
import io.github.wulkanowy.api.login.NotLoggedInException as ScrapperNotLoggedInException
import io.github.wulkanowy.api.interceptor.VulcanException as ScrapperVulcanException
import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.SingleTransformer

class ScrapperExceptionTransformer<T : Any?> : SingleTransformer<T, T> {

    override fun apply(upstream: Single<T>): SingleSource<T> {
        return upstream.onErrorResumeNext {
            if (it is ScrapperVulcanException) Single.error(when (it) {
                is ScrapperFeatureDisabledException -> FeatureDisabledException(it.message ?: "")
                is ScrapperNotLoggedInException -> NotLoggedInException(it.message ?: "")
                is ScrapperServiceUnavailableException -> ServiceUnavailableException(it.message ?: "")
                is ScrapperBadCredentialsException -> BadCredentialsException(it.message ?: "")
                else -> VulcanException(it.message ?: "")
            })
            else Single.error(it)
        }
    }
}

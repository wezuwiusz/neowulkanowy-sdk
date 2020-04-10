package io.github.wulkanowy.sdk.exception

import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.SingleTransformer
import io.github.wulkanowy.sdk.scrapper.ScrapperException as ScrapperApiException
import io.github.wulkanowy.sdk.scrapper.interceptor.FeatureDisabledException as ScrapperFeatureDisabledException
import io.github.wulkanowy.sdk.scrapper.interceptor.ServiceUnavailableException as ScrapperServiceUnavailableException
import io.github.wulkanowy.sdk.scrapper.interceptor.VulcanException as ScrapperVulcanException
import io.github.wulkanowy.sdk.scrapper.login.BadCredentialsException as ScrapperBadCredentialsException
import io.github.wulkanowy.sdk.scrapper.login.NotLoggedInException as ScrapperNotLoggedInException

class ScrapperExceptionTransformer<T : Any?> : SingleTransformer<T, T> {

    override fun apply(upstream: Single<T>): SingleSource<T> {
        return upstream.onErrorResumeNext {
            Single.error(when (it) {
                is ScrapperFeatureDisabledException -> FeatureDisabledException(it.message.orEmpty(), it)
                is ScrapperNotLoggedInException -> NotLoggedInException(it.message.orEmpty(), it)
                is ScrapperServiceUnavailableException -> ServiceUnavailableException(it.message.orEmpty(), it)
                is ScrapperBadCredentialsException -> BadCredentialsException(it.message.orEmpty(), it)
                is ScrapperVulcanException -> VulcanException(it.message.orEmpty(), it)
                is ScrapperApiException -> ApiException(it.message.orEmpty(), it)
                else -> it
            })
        }
    }
}

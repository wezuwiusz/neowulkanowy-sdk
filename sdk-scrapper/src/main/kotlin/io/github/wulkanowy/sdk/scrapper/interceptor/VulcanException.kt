package io.github.wulkanowy.sdk.scrapper.interceptor

import io.github.wulkanowy.sdk.scrapper.ScrapperException

open class VulcanException internal constructor(message: String) : ScrapperException(message)

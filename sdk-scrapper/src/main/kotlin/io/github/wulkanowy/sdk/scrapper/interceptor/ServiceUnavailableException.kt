package io.github.wulkanowy.sdk.scrapper.interceptor

import io.github.wulkanowy.sdk.scrapper.ScrapperException

class ServiceUnavailableException internal constructor(message: String) : ScrapperException(message)

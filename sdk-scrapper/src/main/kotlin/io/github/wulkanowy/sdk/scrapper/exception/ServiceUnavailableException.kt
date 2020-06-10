package io.github.wulkanowy.sdk.scrapper.exception

import io.github.wulkanowy.sdk.scrapper.ScrapperException

class ServiceUnavailableException internal constructor(message: String) : ScrapperException(message)

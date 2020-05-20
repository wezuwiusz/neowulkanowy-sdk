package io.github.wulkanowy.sdk.scrapper.exception

import io.github.wulkanowy.sdk.scrapper.ScrapperException

open class InvalidPathException internal constructor(message: String) : ScrapperException(message)

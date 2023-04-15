package io.github.wulkanowy.sdk.scrapper.login

import io.github.wulkanowy.sdk.scrapper.exception.ScrapperException

class InvalidSymbolException internal constructor(message: String) : ScrapperException(message)

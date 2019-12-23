package io.github.wulkanowy.sdk.scrapper.login

import io.github.wulkanowy.sdk.scrapper.ScrapperException

class BadCredentialsException internal constructor(message: String) : ScrapperException(message)

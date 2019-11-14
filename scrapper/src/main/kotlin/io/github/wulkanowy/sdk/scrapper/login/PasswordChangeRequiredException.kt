package io.github.wulkanowy.sdk.scrapper.login

import io.github.wulkanowy.sdk.scrapper.ScrapperException

class PasswordChangeRequiredException internal constructor(message: String, val redirectUrl: String) : ScrapperException(message)

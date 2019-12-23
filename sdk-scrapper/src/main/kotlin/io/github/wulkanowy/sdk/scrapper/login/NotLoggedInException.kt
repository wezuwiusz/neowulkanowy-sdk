package io.github.wulkanowy.sdk.scrapper.login

import io.github.wulkanowy.sdk.scrapper.ScrapperException

class NotLoggedInException internal constructor(message: String) : ScrapperException(message)

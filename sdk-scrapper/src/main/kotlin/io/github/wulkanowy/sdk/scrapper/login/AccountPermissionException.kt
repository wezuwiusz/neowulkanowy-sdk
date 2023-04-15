package io.github.wulkanowy.sdk.scrapper.login

import io.github.wulkanowy.sdk.scrapper.exception.ScrapperException

class AccountPermissionException internal constructor(message: String) : ScrapperException(message)

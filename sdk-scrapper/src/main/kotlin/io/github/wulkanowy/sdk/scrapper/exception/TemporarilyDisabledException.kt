package io.github.wulkanowy.sdk.scrapper.exception

import io.github.wulkanowy.sdk.scrapper.interceptor.VulcanException

class TemporarilyDisabledException(message: String) : VulcanException(message)

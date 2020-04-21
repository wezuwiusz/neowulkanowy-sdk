package io.github.wulkanowy.sdk.exception

class PasswordChangeRequiredException constructor(message: String, val redirectUrl: String, cause: Throwable?) : VulcanException(message, cause)

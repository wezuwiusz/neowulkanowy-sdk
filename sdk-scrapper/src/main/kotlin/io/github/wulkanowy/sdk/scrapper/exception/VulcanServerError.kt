package io.github.wulkanowy.sdk.scrapper.exception

class VulcanServerError internal constructor(
    message: String,
    val httpCode: Int,
) : VulcanException(message)

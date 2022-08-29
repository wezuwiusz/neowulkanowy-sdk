package io.github.wulkanowy.sdk.scrapper.exception

class VulcanClientError internal constructor(
    message: String,
    val httpCode: Int,
) : VulcanException(message)

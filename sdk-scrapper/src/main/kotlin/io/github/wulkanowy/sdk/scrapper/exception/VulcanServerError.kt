package io.github.wulkanowy.sdk.scrapper.exception

import org.jsoup.nodes.Document

class VulcanServerError internal constructor(
    message: String,
    val doc: Document,
    val httpCode: Int,
) : VulcanException(message)

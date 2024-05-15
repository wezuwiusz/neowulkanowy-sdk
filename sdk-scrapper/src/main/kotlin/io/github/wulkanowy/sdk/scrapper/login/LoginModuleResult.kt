package io.github.wulkanowy.sdk.scrapper.login

import okhttp3.HttpUrl
import org.jsoup.nodes.Document

internal data class LoginModuleResult(
    val moduleUrl: HttpUrl,
    val document: Document,
)

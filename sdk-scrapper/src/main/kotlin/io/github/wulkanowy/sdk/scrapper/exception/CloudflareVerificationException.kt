package io.github.wulkanowy.sdk.scrapper.exception

import java.io.IOException

class CloudflareVerificationException(val originalUrl: String?, override val cause: Throwable) : IOException(cause)

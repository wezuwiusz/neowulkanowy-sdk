package io.github.wulkanowy.sdk.scrapper.exception

import java.io.IOException

class CloudflareVerificationException(override val cause: Throwable) : IOException(cause)

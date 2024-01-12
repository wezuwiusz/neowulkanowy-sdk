package io.github.wulkanowy.sdk.scrapper.exception

import java.io.IOException

class CloudflareVerificationException(val originalUrl: String?) : IOException()

package io.github.wulkanowy.sdk.mobile.exception

import java.io.IOException

open class InvalidTokenException internal constructor(message: String) : IOException(message)

package io.github.wulkanowy.sdk.exception

import java.io.IOException

open class ApiException internal constructor(message: String, cause: Throwable? = null) : IOException(message, cause)

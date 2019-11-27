package io.github.wulkanowy.sdk.exception

import java.io.IOException

open class VulcanException internal constructor(message: String) : IOException(message)

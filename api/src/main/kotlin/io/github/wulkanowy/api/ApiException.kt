package io.github.wulkanowy.api

import java.io.IOException

open class ApiException(message: String) : IOException(message)

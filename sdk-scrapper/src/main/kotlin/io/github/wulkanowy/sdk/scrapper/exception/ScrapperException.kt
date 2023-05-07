package io.github.wulkanowy.sdk.scrapper.exception

import java.io.IOException

open class ScrapperException internal constructor(message: String, val code: Int = -1) : IOException(message)

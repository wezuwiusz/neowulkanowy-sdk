package io.github.wulkanowy.sdk.scrapper

import java.io.IOException

open class ScrapperException internal constructor(message: String) : IOException(message)

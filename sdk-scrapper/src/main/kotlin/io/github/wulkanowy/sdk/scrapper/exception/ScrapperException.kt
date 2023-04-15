package io.github.wulkanowy.sdk.scrapper.exception

import java.io.IOException

open class ScrapperException internal constructor(message: String) : IOException(message)

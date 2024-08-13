package io.github.wulkanowy.sdk.prometheus.exception

import java.io.IOException

open class PrometheusException internal constructor(
    message: String,
    val code: Int = -1,
) : IOException(message)

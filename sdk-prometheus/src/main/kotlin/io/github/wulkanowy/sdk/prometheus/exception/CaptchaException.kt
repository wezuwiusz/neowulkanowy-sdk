package io.github.wulkanowy.sdk.prometheus.exception

open class CaptchaException internal constructor(
    message: String,
) : PrometheusException(message)

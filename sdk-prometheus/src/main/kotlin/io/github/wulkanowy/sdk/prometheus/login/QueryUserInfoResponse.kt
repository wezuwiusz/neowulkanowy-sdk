package io.github.wulkanowy.sdk.prometheus.login

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QueryUserInfoResponse(
    @SerialName("ShowCaptcha")
    val showCaptcha: Boolean,
    @SerialName("ExtraMessage")
    val extraMessage: String,
)

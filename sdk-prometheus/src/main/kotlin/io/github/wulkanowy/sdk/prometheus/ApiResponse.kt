package io.github.wulkanowy.sdk.prometheus

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ApiResponse<T> {
    @SerialName("success")
    val success: Boolean = true

    @SerialName("data")
    val data: T? = null
}

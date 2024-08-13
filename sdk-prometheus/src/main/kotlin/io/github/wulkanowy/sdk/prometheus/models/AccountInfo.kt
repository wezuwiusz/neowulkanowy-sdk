package io.github.wulkanowy.sdk.prometheus.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccountInfo(
    @SerialName("uuid")
    val uuid: String,
    @SerialName("login")
    val login: String,
    @SerialName("accountType")
    val accountType: String,
    @SerialName("firstName")
    val firstName: String? = null,
    @SerialName("surname")
    val surname: String? = null,
    @SerialName("email")
    val email: String,
)

package io.github.wulkanowy.sdk.scrapper.register

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AuthorizePermissionRequest(
    @SerialName("data")
    val data: AuthorizePermission,
)

@Serializable
internal data class AuthorizePermission(
    @SerialName("Pesel")
    val pesel: String,
)

@Serializable
internal data class AuthorizePermissionResponse(
    @SerialName("success")
    val success: Boolean,
)

@Serializable
internal data class AuthorizePermissionPlusRequest(
    val key: String,
    val pesel: String,
)

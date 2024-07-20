package io.github.wulkanowy.sdk.hebe.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Teacher(
    @SerialName("Id")
    val id: Int,
    @SerialName("Name")
    val name: String? = "",
    @SerialName("Surname")
    val surname: String? = "",
    @SerialName("DisplayName")
    val displayName: String,
    @SerialName("Position")
    val position: Int,
    @SerialName("Description")
    val description: String,
)

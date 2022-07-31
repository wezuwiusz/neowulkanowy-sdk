package io.github.wulkanowy.sdk.scrapper.messages

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Sender(

    @SerialName("Id")
    val id: String? = null,

    @SerialName("Name")
    val name: String? = null,

    @SerialName("IdLogin")
    val loginId: Int? = null,

    @SerialName("UnitId")
    val reportingUnitId: Int? = null,

    @SerialName("Role")
    val role: Int? = null,

    @SerialName("Hash")
    val hash: String? = null
)

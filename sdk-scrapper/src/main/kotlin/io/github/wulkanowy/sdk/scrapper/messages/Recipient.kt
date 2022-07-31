package io.github.wulkanowy.sdk.scrapper.messages

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Recipient(

    @SerialName("Id")
    val id: String,

    @SerialName("Name")
    val name: String,

    @SerialName("IdLogin")
    val loginId: Int,

    @SerialName("UnitId")
    val reportingUnitId: Int?,

    @SerialName("Role")
    val role: Int,

    @SerialName("Hash")
    val hash: String,

    @Transient
    val shortName: String? = ""
)

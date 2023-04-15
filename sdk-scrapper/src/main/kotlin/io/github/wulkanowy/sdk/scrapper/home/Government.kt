package io.github.wulkanowy.sdk.scrapper.home

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GovernmentUnit(

    @SerialName("UnitName")
    val unitName: String,

    @SerialName("People")
    val people: List<GovernmentMember>,
)

@Serializable
data class GovernmentMember(

    @SerialName("Name")
    val name: String,

    @SerialName("Position")
    val position: String,

    @SerialName("Division")
    val division: String,

    @SerialName("Id")
    val id: Int,
)

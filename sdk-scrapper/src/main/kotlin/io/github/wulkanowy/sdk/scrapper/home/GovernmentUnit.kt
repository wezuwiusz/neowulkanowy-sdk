package io.github.wulkanowy.sdk.scrapper.home

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GovernmentUnit(

    @SerialName("UnitName")
    val unitName: String,

    @SerialName("People")
    val people: List<GovernmentMember>
)

package io.github.wulkanowy.sdk.scrapper.home

import com.google.gson.annotations.SerializedName

data class GovernmentUnit(

    @SerializedName("UnitName")
    val unitName: String,

    @SerializedName("People")
    val people: List<GovernmentMember>
)

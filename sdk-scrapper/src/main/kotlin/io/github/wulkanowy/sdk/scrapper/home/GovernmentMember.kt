package io.github.wulkanowy.sdk.scrapper.home

import com.google.gson.annotations.SerializedName

data class GovernmentMember(

    @SerializedName("Name")
    val name: String,

    @SerializedName("Position")
    val position: String,

    @SerializedName("Division")
    val division: String,

    @SerializedName("Id")
    val id: Int
)

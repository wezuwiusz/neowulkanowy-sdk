package io.github.wulkanowy.sdk.scrapper.messages

import com.google.gson.annotations.SerializedName

data class Recipient(

    @SerializedName("Id")
    val id: String,

    @SerializedName("Name")
    val name: String,

    @SerializedName("IdLogin")
    val loginId: Int,

    @SerializedName("UnitId")
    val reportingUnitId: Int?,

    @SerializedName("Role")
    val role: Int,

    @SerializedName("Hash")
    val hash: String,

    @Transient
    val shortName: String? = ""
)

package io.github.wulkanowy.sdk.scrapper.messages

import com.google.gson.annotations.SerializedName

data class Sender(

    @SerializedName("Id")
    val id: String? = null,

    @SerializedName("Name")
    val name: String? = null,

    @SerializedName("IdLogin")
    val loginId: Int? = null,

    @SerializedName("UnitId")
    val reportingUnitId: Int? = null,

    @SerializedName("Role")
    val role: Int? = null,

    @SerializedName("Hash")
    val hash: String? = null

)

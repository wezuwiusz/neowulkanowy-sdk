package io.github.wulkanowy.sdk.scrapper.messages

import com.google.gson.annotations.SerializedName

data class Recipient(

    @SerializedName("Id")
    val id: String,

    @SerializedName("Nazwa")
    val name: String,

    @SerializedName("IdLogin")
    val loginId: Int,

    @SerializedName("IdJednostkaSprawozdawcza")
    val reportingUnitId: Int?,

    @SerializedName("Rola")
    val role: Int,

    @SerializedName("Hash")
    val hash: String,

    @Transient
    val shortName: String? = ""
)

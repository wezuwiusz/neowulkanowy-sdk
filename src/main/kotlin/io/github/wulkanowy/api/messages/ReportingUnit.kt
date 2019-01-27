package io.github.wulkanowy.api.messages

import com.google.gson.annotations.SerializedName

data class ReportingUnit(

    @SerializedName("IdJednostkaSprawozdawcza")
    val id: Int,

    @SerializedName("Skrot")
    val short: String,

    @SerializedName("Id")
    val senderId: Int
)

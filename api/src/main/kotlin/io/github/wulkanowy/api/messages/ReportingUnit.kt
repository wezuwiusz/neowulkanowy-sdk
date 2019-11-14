package io.github.wulkanowy.api.messages

import com.google.gson.annotations.SerializedName

data class ReportingUnit(

    @SerializedName("IdJednostkaSprawozdawcza")
    val id: Int = 0,

    @SerializedName("Skrot")
    val short: String = "",

    @SerializedName("Id")
    val senderId: Int = 0,

    @SerializedName("Role")
    val roles: List<Int> = emptyList(),

    @SerializedName("NazwaNadawcy")
    val senderName: String = ""
)

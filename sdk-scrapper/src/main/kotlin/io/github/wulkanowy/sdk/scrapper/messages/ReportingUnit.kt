package io.github.wulkanowy.sdk.scrapper.messages

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReportingUnit(

    @SerialName("IdJednostkaSprawozdawcza")
    val unitId: Int = 0,

    @SerialName("Skrot")
    val short: String = "",

    @SerialName("Id")
    val senderId: Int = 0,

    @SerialName("Role")
    val roles: List<Int> = emptyList(),

    @SerialName("NazwaNadawcy")
    val senderName: String = ""
)

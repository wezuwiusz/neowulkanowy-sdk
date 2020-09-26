package io.github.wulkanowy.sdk.scrapper.messages

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReportingUnit(

    @Json(name = "IdJednostkaSprawozdawcza")
    val unitId: Int = 0,

    @Json(name = "Skrot")
    val short: String = "",

    @Json(name = "Id")
    val senderId: Int = 0,

    @Json(name = "Role")
    val roles: List<Int> = emptyList(),

    @Json(name = "NazwaNadawcy")
    val senderName: String = ""
)

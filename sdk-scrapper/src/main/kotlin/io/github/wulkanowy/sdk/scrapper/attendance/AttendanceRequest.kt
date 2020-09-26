package io.github.wulkanowy.sdk.scrapper.attendance

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class AttendanceRequest(

    @Json(name = "data")
    val date: Date,

    @Json(name = "idTypWpisuFrekwencji")
    val typeId: Int = -1
)

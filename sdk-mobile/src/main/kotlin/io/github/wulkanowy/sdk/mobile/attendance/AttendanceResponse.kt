package io.github.wulkanowy.sdk.mobile.attendance

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AttendanceResponse(

    @Json(name = "DataPoczatkowa")
    val dateStart: Long,

    @Json(name = "DataPoczatkowaTekst")
    val dateStartText: String,

    @Json(name = "DataKoncowa")
    val dateEnd: Long,

    @Json(name = "DataKoncowaTekst")
    val dateEndText: String,

    @Json(name = "Frekwencje")
    val items: List<Attendance>,
)

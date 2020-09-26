package io.github.wulkanowy.sdk.scrapper.attendance

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AttendanceResponse(

    @Json(name = "UsprawiedliwieniaAktywne")
    val excuseActive: Boolean,

    @Json(name = "Frekwencje")
    val lessons: List<Attendance>,

    @Json(name = "UsprawiedliwieniaWyslane")
    val sentExcuses: List<SentExcuse>
)

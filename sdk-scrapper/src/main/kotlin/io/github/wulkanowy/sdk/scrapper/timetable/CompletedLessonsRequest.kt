package io.github.wulkanowy.sdk.scrapper.timetable

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class CompletedLessonsRequest(

    @Json(name = "poczatek")
    val startDate: String,

    @Json(name = "koniec")
    val endDate: String,

    @Json(name = "idPrzedmiot")
    val subject: Int = -1
)

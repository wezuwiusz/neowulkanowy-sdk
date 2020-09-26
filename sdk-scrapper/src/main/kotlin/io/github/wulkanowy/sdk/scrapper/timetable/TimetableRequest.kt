package io.github.wulkanowy.sdk.scrapper.timetable

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TimetableRequest(

    @Json(name = "data")
    val date: String
)

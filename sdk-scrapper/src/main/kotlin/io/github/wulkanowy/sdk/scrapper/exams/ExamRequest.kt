package io.github.wulkanowy.sdk.scrapper.exams

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class ExamRequest(

    @Json(name = "data")
    val date: Date,

    @Json(name = "rokSzkolny")
    val schoolYear: Int
)

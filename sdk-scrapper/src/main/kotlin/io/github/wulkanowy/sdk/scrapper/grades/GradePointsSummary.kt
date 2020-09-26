package io.github.wulkanowy.sdk.scrapper.grades

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GradePointsSummary(

    val semesterId: Int = 0,

    @Json(name = "Subject")
    val subject: String,

    @Json(name = "Value1")
    val others: Double,

    @Json(name = "Value2")
    val student: Double
)

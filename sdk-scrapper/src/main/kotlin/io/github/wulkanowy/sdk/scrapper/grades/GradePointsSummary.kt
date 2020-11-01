package io.github.wulkanowy.sdk.scrapper.grades

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GradePointsSummaryResponse(

    @Json(name = "TableContent")
    val tableContent: String,

    @Json(name = "Items")
    val items: List<GradePointsSummary>
)

@JsonClass(generateAdapter = true)
data class GradePointsSummary(

    @Json(name = "Subject")
    val subject: String,

    @Json(name = "Value1")
    val others: Double,

    @Json(name = "Value2")
    val student: Double
)

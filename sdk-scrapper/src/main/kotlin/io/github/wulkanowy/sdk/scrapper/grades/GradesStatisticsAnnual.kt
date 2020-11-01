package io.github.wulkanowy.sdk.scrapper.grades

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GradesStatisticsAnnual(

    @Json(name = "Subject")
    val subject: String,

    @Json(name = "IsEmpty")
    val isEmpty: Boolean,

    @Json(name = "Items")
    val items: List<GradeStatisticsItemAnnual>?
)

@JsonClass(generateAdapter = true)
data class GradeStatisticsItemAnnual(

    @Json(name = "Label")
    val label: String,

    @Json(name = "Description")
    val description: String,

    @Json(name = "Value")
    val value: Int
)

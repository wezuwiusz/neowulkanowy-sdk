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
    val items: List<GradeStatistics>?
)

@JsonClass(generateAdapter = true)
data class GradesStatisticsPartial(

    @Json(name = "Subject")
    val subject: String,

    @Json(name = "IsAverage")
    val isAverage: Boolean,

    @Json(name = "ClassSeries")
    val classSeries: Series,

    @Json(name = "StudentSeries")
    val studentSeries: Series
)

@JsonClass(generateAdapter = true)
data class Series(

    @Json(name = "Average")
    val average: String?,

    @Json(name = "IsEmpty")
    val isEmpty: Boolean,

    @Json(name = "Items")
    val items: List<GradeStatistics>?
)

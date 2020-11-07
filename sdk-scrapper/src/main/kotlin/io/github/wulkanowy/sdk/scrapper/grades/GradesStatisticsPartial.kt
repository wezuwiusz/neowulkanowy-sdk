package io.github.wulkanowy.sdk.scrapper.grades

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GradesStatisticsPartial(

    @Json(name = "Subject")
    val subject: String,

    @Json(name = "IsAverage")
    val isAverage: Boolean,

    @Json(name = "ClassSeries")
    val classSeries: GradeStatisticsPartialSeries,

    @Json(name = "StudentSeries")
    val studentSeries: GradeStatisticsPartialSeries
)

@JsonClass(generateAdapter = true)
data class GradeStatisticsPartialSeries(

    @Json(name = "Average")
    val average: String?,

    @Json(name = "IsEmpty")
    val isEmpty: Boolean,

    @Json(name = "Items")
    val items: List<GradeStatisticsPartialItem>?
)

@JsonClass(generateAdapter = true)
data class GradeStatisticsPartialItem(

    @Json(name = "Label")
    val label: String,

    @Json(name = "Value")
    val amount: Int? = 0
) {

    @Transient
    var grade: Int = 0
}

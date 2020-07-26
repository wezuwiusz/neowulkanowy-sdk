package io.github.wulkanowy.sdk.scrapper.grades

import com.google.gson.annotations.SerializedName

data class GradesStatisticsAnnual(

    @SerializedName("Subject")
    val subject: String,

    @SerializedName("IsEmpty")
    val isEmpty: Boolean,

    @SerializedName("Items")
    val items: List<GradeStatistics>?
)

data class GradesStatisticsPartial(

    @SerializedName("Subject")
    val subject: String,

    @SerializedName("IsAverage")
    val isAverage: Boolean,

    @SerializedName("ClassSeries")
    val classSeries: Series,

    @SerializedName("StudentSeries")
    val studentSeries: Series
)

data class Series(

    @SerializedName("Average")
    val average: String?,

    @SerializedName("IsEmpty")
    val isEmpty: Boolean,

    @SerializedName("Items")
    val items: List<GradeStatistics>?
)

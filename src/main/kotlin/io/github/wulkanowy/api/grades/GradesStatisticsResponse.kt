package io.github.wulkanowy.api.grades

import com.google.gson.annotations.SerializedName
import pl.droidsonroids.jspoon.annotation.Selector

class GradesStatisticsResponse {

    @Selector("#okresyKlasyfikacyjneDropDownList option[selected]", attr = "value")
    var semesterId: Int = 0

    @Selector(".mainContainer > div table tbody tr")
    var items: List<GradeStatistics> = emptyList()

    data class Annual(

        @SerializedName("Subject")
        val subject: String,

        @SerializedName("IsEmpty")
        val isEmpty: Boolean,

        @SerializedName("Items")
        val items: List<GradeStatistics>?
    )

    data class Points(

        @SerializedName("Subject")
        val subject: String,

        @SerializedName("Label1")
        val percentage: String
    )

    data class Partial(

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
}

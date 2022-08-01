package io.github.wulkanowy.sdk.scrapper.grades

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class GradesStatisticsPartial(

    @SerialName("Subject")
    val subject: String,

    @SerialName("IsAverage")
    val isAverage: Boolean,

    @SerialName("ClassSeries")
    val classSeries: GradeStatisticsPartialSeries,

    @SerialName("StudentSeries")
    val studentSeries: GradeStatisticsPartialSeries
)

@Serializable
data class GradeStatisticsPartialSeries(

    @SerialName("Average")
    val average: String?,

    @SerialName("IsEmpty")
    val isEmpty: Boolean,

    @SerialName("Items")
    val items: List<GradeStatisticsPartialItem>?
)

@Serializable
data class GradeStatisticsPartialItem(

    @SerialName("Label")
    val label: String,

    @SerialName("Value")
    val amount: Int? = 0
) {

    @Transient
    var grade: Int = 0
}

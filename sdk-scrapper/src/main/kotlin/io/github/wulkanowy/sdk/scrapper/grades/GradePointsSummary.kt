package io.github.wulkanowy.sdk.scrapper.grades

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GradePointsSummaryResponse(

    @SerialName("Items")
    val items: List<GradePointsSummary>,
)

@Serializable
data class GradePointsSummary(

    @SerialName("Subject")
    val subject: String,

    @SerialName("Value1")
    val others: Double,

    @SerialName("Value2")
    val student: Double,
)

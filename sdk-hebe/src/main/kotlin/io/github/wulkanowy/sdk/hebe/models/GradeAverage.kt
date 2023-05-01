package io.github.wulkanowy.sdk.hebe.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GradeAverage(
    @SerialName("Average")
    val average: String,
    @SerialName("Id")
    val id: Int,
    @SerialName("PeriodId")
    val periodId: Int,
    // @SerialName("Points")
    // val points: Any?,
    @SerialName("PupilId")
    val pupilId: Int,
    @SerialName("Subject")
    val subject: Subject,
) {
    @Serializable
    data class Subject(
        @SerialName("Id")
        val id: Int,
        @SerialName("Key")
        val key: String,
        @SerialName("Kod")
        val kod: String,
        @SerialName("Name")
        val name: String,
        @SerialName("Position")
        val position: Int,
    )
}

package io.github.wulkanowy.sdk.hebe.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GradeSummary(
    @SerialName("DateModify")
    val dateModify: DateModify,
    @SerialName("Entry_1")
    val entry1: String? = null,
    @SerialName("Entry_2")
    val entry2: String? = null,
    // @SerialName("Entry_3")
    // val entry3: Any?,
    @SerialName("Id")
    val id: Int,
    @SerialName("PeriodId")
    val periodId: Int,
    @SerialName("PupilId")
    val pupilId: Int,
    @SerialName("Subject")
    val subject: Subject,
) {
    @Serializable
    data class DateModify(
        @SerialName("Date")
        val date: String,
        @SerialName("DateDisplay")
        val dateDisplay: String,
        @SerialName("Time")
        val time: String,
        @SerialName("Timestamp")
        val timestamp: Long,
    )

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

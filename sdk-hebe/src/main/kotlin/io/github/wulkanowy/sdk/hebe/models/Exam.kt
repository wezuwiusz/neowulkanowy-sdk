package io.github.wulkanowy.sdk.hebe.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Exam(
    @SerialName("Content")
    val content: String,
    @SerialName("Creator")
    val creator: Creator,
    @SerialName("DateCreated")
    val dateCreated: DateCreated,
    @SerialName("DateModify")
    val dateModify: DateModify,
    @SerialName("Deadline")
    val deadline: Deadline,
    @SerialName("Id")
    val id: Int,
    @SerialName("Key")
    val key: String,
    @SerialName("PupilId")
    val pupilId: Int,
    @SerialName("Subject")
    val subject: Subject,
    @SerialName("Type")
    val type: String,
) {
    @Serializable
    data class Creator(
        @SerialName("DisplayName")
        val displayName: String,
        @SerialName("Id")
        val id: Int,
        @SerialName("Name")
        val name: String,
        @SerialName("Surname")
        val surname: String,
    )

    @Serializable
    data class DateCreated(
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
    data class Deadline(
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

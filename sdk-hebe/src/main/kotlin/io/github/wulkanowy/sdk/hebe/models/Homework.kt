package io.github.wulkanowy.sdk.hebe.models

import io.github.wulkanowy.sdk.hebe.CustomDateAdapter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Homework(
    @SerialName("Id")
    val id: Int,
    @SerialName("Key")
    val key: String,
    @SerialName("IdPupil")
    val idPupil: Int,
    @SerialName("IdHomework")
    val idHomework: Int,
    @SerialName("Content")
    val content: String,
    @SerialName("IsAnswerRequired")
    val isAnswerRequired: Boolean,
    @SerialName("DateCreated")
    val dateCreated: DateCreated,
    @SerialName("Date")
    val date: Date,
    @SerialName("AnswerDate")
    val answerDate: AnswerDate?,
    @SerialName("Deadline")
    val deadline: Deadline,
    @SerialName("Creator")
    val creator: Creator,
    @SerialName("Subject")
    val subject: Subject,
    /* TODO: There is an array of Attachments in the API response, but I don't have any
    homework with attachments on my account, so I cannot add it to the model. Contributions welcome.
     */
) {
    @Serializable
    data class DateCreated(
        @SerialName("Date")
        @Serializable(with = CustomDateAdapter::class)
        val date: LocalDate,
        @SerialName("DateDisplay")
        val dateDisplay: String,
        @SerialName("Time")
        val time: String,
        @SerialName("Timestamp")
        val timestamp: Long,
    )

    @Serializable
    data class Date(
        @SerialName("Date")
        @Serializable(with = CustomDateAdapter::class)
        val date: LocalDate,
        @SerialName("DateDisplay")
        val dateDisplay: String,
        @SerialName("Time")
        val time: String,
        @SerialName("Timestamp")
        val timestamp: Long,
    )

    @Serializable
    data class AnswerDate(
        @SerialName("Date")
        @Serializable(with = CustomDateAdapter::class)
        val date: LocalDate,
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
        @Serializable(with = CustomDateAdapter::class)
        val date: LocalDate,
        @SerialName("DateDisplay")
        val dateDisplay: String,
        @SerialName("Time")
        val time: String,
        @SerialName("Timestamp")
        val timestamp: Long,
    )

    @Serializable
    data class Creator(
        @SerialName("Id")
        val id: Int,
        @SerialName("Surname")
        val surname: String,
        @SerialName("Name")
        val name: String,
        @SerialName("DisplayName")
        val displayName: String,
    )

    @Serializable
    data class Subject(
        @SerialName("Id")
        val id: Int,
        @SerialName("Key")
        val key: String,
        @SerialName("Name")
        val name: String,
        @SerialName("Kod")
        val kod: String,
        @SerialName("Position")
        val position: Int,
    )
}

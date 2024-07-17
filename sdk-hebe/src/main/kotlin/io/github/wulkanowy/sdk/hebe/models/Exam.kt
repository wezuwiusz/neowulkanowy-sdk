package io.github.wulkanowy.sdk.hebe.models

import io.github.wulkanowy.sdk.hebe.CustomDateAdapter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Exam(
    @SerialName("Id")
    val id: Int,
    @SerialName("Key")
    val key: String,
    @SerialName("Type")
    val type: String,
    @SerialName("TypeId")
    val typeId: Int,
    @SerialName("Content")
    val content: String,
    @SerialName("DateCreated")
    val dateCreated: Date,
    @SerialName("DateModify")
    val dateModify: Date,
    @SerialName("Deadline")
    val deadline: Date,
    @SerialName("Creator")
    val creator: Creator,
    @SerialName("Subject")
    val subject: Subject,
    @SerialName("PupilId")
    val pupilId: Int,
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

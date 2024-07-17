package io.github.wulkanowy.sdk.hebe.models

import io.github.wulkanowy.sdk.hebe.CustomDateAdapter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Lesson(
    @SerialName("Id")
    val id: Int,
    @SerialName("MergeChangeId")
    val mergeChangeId: Int?,
    @SerialName("Date")
    val date: Date,
    @SerialName("Room")
    val room: Room? = null,
    @SerialName("TimeSlot")
    val timeSlot: TimeSlot,
    @SerialName("Subject")
    val subject: Subject,
    @SerialName("TeacherPrimary")
    val teacherPrimary: Teacher,
    @SerialName("TeacherSecondary")
    val teacherSecondary: Teacher?,
    @SerialName("TeacherSecondary2")
    val teacherTertiary: Teacher?,
    @SerialName("Clazz")
    val `class`: Class,
    @SerialName("PupilAlias")
    val pupilAlias: String?,
    @SerialName("Visible")
    val visible: Boolean,
    @SerialName("Distribution")
    val distribution: Distribution?,
    @SerialName("Change")
    val change: Change?,
) {
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
    data class Room(
        @SerialName("Id")
        val id: Int,
        @SerialName("Code")
        val code: String,
    )

    @Serializable
    data class TimeSlot(
        @SerialName("Id")
        val id: Int,
        @SerialName("Start")
        val start: String,
        @SerialName("End")
        val end: String,
        @SerialName("Display")
        val display: String,
        @SerialName("Position")
        val position: Int,
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

    @Serializable
    data class Teacher(
        @SerialName("Id")
        val id: Int,
        @SerialName("Name")
        val name: String,
        @SerialName("Surname")
        val surname: String,
        @SerialName("DisplayName")
        val displayName: String,
    )

    @Serializable
    data class Class(
        @SerialName("Id")
        val id: Int,
        @SerialName("Key")
        val key: String,
        @SerialName("DisplayName")
        val displayName: String,
        @SerialName("Symbol")
        val symbol: String,
    )

    @Serializable
    data class Distribution(
        @SerialName("Id")
        val id: Int,
        @SerialName("Key")
        val key: String,
        @SerialName("Name")
        val name: String,
        @SerialName("Shortcut")
        val shortcut: String,
        @SerialName("PartType")
        val partType: String,
    )

    @Serializable
    data class Change(
        @SerialName("Id")
        val id: Int,
        @SerialName("Type")
        val type: Int,
        @SerialName("IsMerge")
        val isMerge: Boolean,
        @SerialName("Separation")
        val separation: Boolean,
    )
}

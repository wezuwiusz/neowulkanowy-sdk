package io.github.wulkanowy.sdk.hebe.models

import io.github.wulkanowy.sdk.hebe.CustomDateAdapter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class TimetableChange(
    @SerialName("Id")
    val id: Int,
    @SerialName("UnitId")
    val unitId: Int,
    @SerialName("ScheduleId")
    val scheduleId: Int,
    @SerialName("LessonDate")
    val lessonDate: Date,
    @SerialName("ChangeDate")
    val changeDate: Date?,
    @SerialName("Reason")
    val reason: String? = null,
    @SerialName("Room")
    val room: Room? = null,
    @SerialName("Subject")
    val subject: Subject? = null,
    @SerialName("TeacherPrimary")
    val teacherPrimary: Teacher? = null,
    @SerialName("TeacherAbsenceReasonId")
    val teacherAbsenceReasonId: Int? = null,
    @SerialName("TeacherAbsenceEffectName")
    val teacherAbsenceEffectName: String? = null,
    @SerialName("TeacherSecondary")
    val teacherSecondary: Teacher? = null,
    @SerialName("TeacherSecondaryAbsenceReasonId")
    val teacherSecondaryAbsenceReasonId: Int? = null,
    @SerialName("TeacherSecondaryAbsenceEffectName")
    val teacherSecondaryAbsenceEffectName: String? = null,
    @SerialName("TeacherSecondary2")
    val teacherTertiary: Teacher? = null,
    @SerialName("TeacherSecondary2AbsenceReasonId")
    val teacherSecondary2AbsenceReasonId: Int? = null,
    @SerialName("TeacherSecondary2AbsenceEffectName")
    val teacherSecondary2AbsenceEffectName: String? = null,
    @SerialName("Change")
    val change: Change,
    @SerialName("Clazz")
    val `class`: Class,
    @SerialName("Distribution")
    val distribution: Distribution? = null,
    @SerialName("ClassAbsence")
    val classAbsence: Boolean? = null,
    @SerialName("NoRoom")
    val noRoom: Boolean? = null,
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
}

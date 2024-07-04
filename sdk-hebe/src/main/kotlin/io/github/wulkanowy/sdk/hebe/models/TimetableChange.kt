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
    val lessonDate: LessonDate,
    @SerialName("ChangeDate")
    val changeDate: ChangeDate?,
    @SerialName("Reason")
    val reason: String?,
    @SerialName("Room")
    val room: Room?,
    @SerialName("Subject")
    val subject: String?,
    @SerialName("TeacherPrimary")
    val teacherPrimary: Teacher?,
    @SerialName("TeacherAbsenceReasonId")
    val teacherAbsenceReasonId: Int,
    @SerialName("TeacherAbsenceEffectName")
    val teacherAbsenceEffectName: String,
    @SerialName("TeacherSecondary")
    val teacherSecondary: Teacher?,
    @SerialName("TeacherSecondaryAbsenceReasonId")
    val teacherSecondaryAbsenceReasonId: Int?,
    @SerialName("TeacherSecondaryAbsenceEffectName")
    val teacherSecondaryAbsenceEffectName: String?,
    @SerialName("TeacherSecondary2")
    val teacherTertiary: Teacher?,
    @SerialName("TeacherSecondary2AbsenceReasonId")
    val teacherSecondary2AbsenceReasonId: Int?,
    @SerialName("TeacherSecondary2AbsenceEffectName")
    val teacherSecondary2AbsenceEffectName: String?,
    @SerialName("Change")
    val change: Change,
    @SerialName("Clazz")
    val `class`: Class,
    @SerialName("Distribution")
    val distribution: Distribution,
    @SerialName("ClassAbsence")
    val classAbsence: Boolean,
    @SerialName("NoRoom")
    val noRoom: Boolean,
) {
    @Serializable
    data class LessonDate(
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
    data class ChangeDate(
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

package io.github.wulkanowy.sdk.hebe.models

import io.github.wulkanowy.sdk.hebe.CustomDateAdapter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class CompletedLesson(
    @SerialName("LessonId")
    val lessonId: Int,
    @SerialName("PresenceType")
    val presenceType: PresenceType? = null,
    @SerialName("Id")
    val id: Int,
    @SerialName("LessonClassId")
    val lessonClassId: Int,
    @SerialName("Day")
    val day: Date,
    @SerialName("CalculatePresence")
    val calculatePresence: Boolean? = null,
    @SerialName("GroupDefinition")
    val groupDefinition: String,
    @SerialName("Replacement")
    val replacement: Boolean? = null,
    @SerialName("DateModify")
    val dateModify: Date,
    @SerialName("GlobalKey")
    val globalKey: String,
    @SerialName("Note")
    val note: String? = null,
    @SerialName("Topic")
    val topic: String? = null,
    @SerialName("LessonNumber")
    val lessonNumber: Int? = null,
    @SerialName("LessonClassGlobalKey")
    val lessonClassGlobalKey: String? = null,
    @SerialName("TimeSlot")
    val timeSlot: TimeSlot,
    @SerialName("Subject")
    val subject: Subject? = null,
    @SerialName("TeacherPrimary")
    val primaryTeacher: Teacher? = null,
    @SerialName("TeacherSecondary")
    val secondaryTeacher: Teacher? = null,
    @SerialName("Clazz")
    val `class`: Class,
    @SerialName("Distribution")
    val distribution: Distribution? = null,
    @SerialName("Visible")
    val visible: Boolean,
) {
    @Serializable
    data class PresenceType(
        @SerialName("Id")
        val id: Int,
        @SerialName("Symbol")
        val symbol: String,
        @SerialName("Name")
        val name: String,
        @SerialName("CategoryId")
        val categoryId: Int,
        @SerialName("CategoryName")
        val categoryName: String,
        @SerialName("Position")
        val position: Int,
        @SerialName("Presence")
        val presence: Boolean,
        @SerialName("Absence")
        val absence: Boolean,
        @SerialName("LegalAbsence")
        val legalAbsence: Boolean,
        @SerialName("Late")
        val late: Boolean,
        @SerialName("AbsenceJustified")
        val absenceJustified: Boolean,
        @SerialName("Removed")
        val removed: Boolean,
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
}

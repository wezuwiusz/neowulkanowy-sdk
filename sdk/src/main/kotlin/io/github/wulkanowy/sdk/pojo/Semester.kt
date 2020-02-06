package io.github.wulkanowy.sdk.pojo

import org.threeten.bp.LocalDate

data class Semester(
    val diaryId: Int,
    val diaryName: String,
    val schoolYear: Int,
    val semesterId: Int,
    val semesterNumber: Int,
    @Deprecated("Use start and end instead")
    val current: Boolean,
    val start: LocalDate,
    val end: LocalDate,
    val classId: Int,
    val unitId: Int,
    val feesEnabled: Boolean,
    val menuEnabled: Boolean,
    val completedLessonsEnabled: Boolean
)

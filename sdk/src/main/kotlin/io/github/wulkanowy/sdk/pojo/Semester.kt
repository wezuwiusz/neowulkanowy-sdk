package io.github.wulkanowy.sdk.pojo

import org.threeten.bp.LocalDate

data class Semester(
    val diaryId: Int,
    val diaryName: String,
    val schoolYear: Int,
    val semesterId: Int,
    val semesterNumber: Int,
    val current: Boolean,
    val start: LocalDate,
    val end: LocalDate,
    val classId: Int,
    val unitId: Int
)

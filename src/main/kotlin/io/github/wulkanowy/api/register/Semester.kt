package io.github.wulkanowy.api.register

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDate.now

data class Semester(
    val diaryId: Int,
    val diaryName: String,
    val semesterId: Int,
    val semesterNumber: Int,
    var current: Boolean = false,
    val start: LocalDate = now(),
    val end: LocalDate = now(),
    val classId: Int = 0,
    val unitId: Int = 0
)

package io.github.wulkanowy.sdk.scrapper.register

import java.time.LocalDate
import java.time.LocalDate.now

data class Semester(
    val diaryId: Int,
    val kindergartenDiaryId: Int,
    val diaryName: String,
    val schoolYear: Int,
    val semesterId: Int,
    val semesterNumber: Int,
    val start: LocalDate = now(),
    val end: LocalDate = now(),
    val classId: Int = 0,
    val unitId: Int = 0,
)

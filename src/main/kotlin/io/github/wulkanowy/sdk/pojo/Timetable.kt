package io.github.wulkanowy.sdk.pojo

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

data class Timetable(
    val number: Int,
    val start: LocalDateTime,
    val end: LocalDateTime,
    val date: LocalDate,
    val subject: String,
    val subjectOld: String,
    val group: String,
    val room: String,
    val roomOld: String,
    val teacher: String,
    val teacherOld: String,
    val info: String,
    val changes: Boolean,
    val canceled: Boolean
)

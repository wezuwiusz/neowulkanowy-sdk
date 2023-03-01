package io.github.wulkanowy.sdk.scrapper.timetable

import java.time.LocalDate
import java.time.LocalDateTime

data class TimetableFull(
    val headers: List<TimetableDayHeader>,
    val lessons: List<Timetable>,
    val additional: List<TimetableAdditional>,
)

data class Timetable(
    val number: Int = 0,
    val start: LocalDateTime,
    val end: LocalDateTime,
    val date: LocalDate,
    val subject: String = "",
    val subjectOld: String = "",
    val group: String = "",
    val room: String = "",
    val roomOld: String = "",
    val teacher: String = "",
    val teacherOld: String = "",
    val info: String = "",
    val changes: Boolean = false,
    val canceled: Boolean = false,
)

data class TimetableDayHeader(
    val date: LocalDate,
    val content: String,
)

data class TimetableAdditional(
    val start: LocalDateTime,
    val end: LocalDateTime,
    val date: LocalDate,
    val subject: String,
)

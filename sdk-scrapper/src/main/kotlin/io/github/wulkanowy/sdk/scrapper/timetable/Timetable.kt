package io.github.wulkanowy.sdk.scrapper.timetable

import java.time.LocalDate
import java.util.Date

data class TimetableFull(
    val headers: List<TimetableDayHeader>,
    val lessons: List<Timetable>,
    val additional: List<TimetableAdditional>
)

data class Timetable(
    val number: Int = 0,
    val start: Date = Date(),
    val end: Date = Date(),
    val date: Date = Date(),
    val subject: String = "",
    val subjectOld: String = "",
    val group: String = "",
    val room: String = "",
    val roomOld: String = "",
    val teacher: String = "",
    val teacherOld: String = "",
    val info: String = "",
    val changes: Boolean = false,
    val canceled: Boolean = false
)

data class TimetableDayHeader(
    val date: LocalDate,
    val content: String
)

data class TimetableAdditional(
    val start: Date,
    val end: Date,
    val date: Date,
    val subject: String
)

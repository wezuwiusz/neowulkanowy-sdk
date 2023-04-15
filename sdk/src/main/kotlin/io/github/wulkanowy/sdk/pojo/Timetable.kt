package io.github.wulkanowy.sdk.pojo

import java.time.LocalDate
import java.time.ZonedDateTime

data class Timetable(
    val headers: List<TimetableDayHeader>,
    val lessons: List<Lesson>,
    val additional: List<LessonAdditional>,
)

data class TimetableDayHeader(
    val date: LocalDate,
    val content: String,
)

data class Lesson(
    val number: Int,
    val start: ZonedDateTime,
    val end: ZonedDateTime,
    val date: LocalDate,
    val subject: String,
    val subjectOld: String,
    val group: String,
    val room: String,
    val roomOld: String,
    val teacher: String,
    val teacherOld: String,
    val info: String,
    val studentPlan: Boolean,
    val changes: Boolean,
    val canceled: Boolean,
)

data class LessonAdditional(
    val start: ZonedDateTime,
    val end: ZonedDateTime,
    val date: LocalDate,
    val subject: String,
)

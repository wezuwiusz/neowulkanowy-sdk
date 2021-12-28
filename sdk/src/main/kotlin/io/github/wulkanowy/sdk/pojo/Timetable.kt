package io.github.wulkanowy.sdk.pojo

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime

data class Timetable(
    val number: Int,
    @Deprecated("use startZoned instead")
    val start: LocalDateTime,
    @Deprecated("use endZoned instead")
    val end: LocalDateTime,
    val startZoned: ZonedDateTime,
    val endZoned: ZonedDateTime,
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
    val canceled: Boolean
)

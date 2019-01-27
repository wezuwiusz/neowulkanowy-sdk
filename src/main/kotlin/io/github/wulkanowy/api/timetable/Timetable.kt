package io.github.wulkanowy.api.timetable

import java.util.Date

data class Timetable(
    val number: Int = 0,
    val start: Date = Date(),
    val end: Date = Date(),
    val date: Date = Date(),
    val subject: String = "",
    val group: String = "",
    val room: String = "",
    val teacher: String = "",
    val info: String = "",
    val changes: Boolean = false,
    val canceled: Boolean = false
)

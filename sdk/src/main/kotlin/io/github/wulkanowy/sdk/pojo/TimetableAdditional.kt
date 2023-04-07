package io.github.wulkanowy.sdk.pojo

import java.time.LocalDate
import java.time.ZonedDateTime

data class TimetableAdditional(
    val start: ZonedDateTime,
    val end: ZonedDateTime,
    val date: LocalDate,
    val subject: String,
)

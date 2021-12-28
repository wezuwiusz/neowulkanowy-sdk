package io.github.wulkanowy.sdk.pojo

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime

data class TimetableAdditional(
    @Deprecated("use startZoned instead")
    val start: LocalDateTime,
    @Deprecated("use endZoned instead")
    val end: LocalDateTime,
    val startZoned: ZonedDateTime,
    val endZoned: ZonedDateTime,
    val date: LocalDate,
    val subject: String
)

package io.github.wulkanowy.sdk.pojo

import java.time.LocalDate
import java.time.LocalDateTime

data class TimetableAdditional(
    val start: LocalDateTime,
    val end: LocalDateTime,
    val date: LocalDate,
    val subject: String
)

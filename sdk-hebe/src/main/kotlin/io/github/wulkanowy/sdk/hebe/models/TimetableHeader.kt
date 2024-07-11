package io.github.wulkanowy.sdk.hebe.models

import java.time.LocalDate

data class TimetableHeader(
    val date: LocalDate,
    val content: String,
)

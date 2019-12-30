package io.github.wulkanowy.sdk.pojo

import org.threeten.bp.LocalDateTime

data class Absent(
    val date: LocalDateTime,
    val timeId: Int?
)

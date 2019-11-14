package io.github.wulkanowy.api.attendance

import org.threeten.bp.LocalDateTime

data class Absent(

    val date: LocalDateTime,
    val timeId: Int?
)

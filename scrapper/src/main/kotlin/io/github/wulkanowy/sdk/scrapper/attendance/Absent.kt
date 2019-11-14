package io.github.wulkanowy.sdk.scrapper.attendance

import org.threeten.bp.LocalDateTime

data class Absent(

    val date: LocalDateTime,
    val timeId: Int?
)

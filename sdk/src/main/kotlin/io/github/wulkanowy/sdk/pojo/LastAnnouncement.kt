package io.github.wulkanowy.sdk.pojo

import java.time.LocalDate

data class LastAnnouncement(
    val date: LocalDate,
    val author: String,
    val content: String,
)

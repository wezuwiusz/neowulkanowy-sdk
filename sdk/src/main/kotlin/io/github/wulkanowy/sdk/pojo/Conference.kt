package io.github.wulkanowy.sdk.pojo

import java.time.LocalDateTime

data class Conference(
    val title: String,
    val subject: String,
    val agenda: String,
    val presentOnConference: String,
    val online: Any?,
    val id: Int,
    val date: LocalDateTime
)

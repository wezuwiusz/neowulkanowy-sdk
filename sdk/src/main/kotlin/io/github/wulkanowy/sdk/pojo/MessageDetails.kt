package io.github.wulkanowy.sdk.pojo

import java.time.LocalDateTime

data class MessageDetails(
    val content: String,
    val attachments: List<MessageAttachment>,
    val apiGlobalKey: String,
    val date: LocalDateTime,
    val sender: String,
    val recipients: List<String>,
    val subject: String,
    val id: Int,
)

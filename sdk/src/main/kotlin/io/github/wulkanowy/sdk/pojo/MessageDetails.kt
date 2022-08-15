package io.github.wulkanowy.sdk.pojo

import java.time.LocalDateTime

data class MessageDetails(
    val content: String,
    val attachments: List<MessageAttachment>,
    val apiGlobalKey: String,
    val date: LocalDateTime,
    val mailboxId: String,
    val senderMailboxId: String,
    val senderMailboxName: String,
    val recipients: List<Recipient>,
    val subject: String,
    val id: Int,
)

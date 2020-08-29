package io.github.wulkanowy.sdk.pojo

import java.time.LocalDateTime

data class Message(
    val id: Int?,
    val messageId: Int?,
    val sender: Sender?,
    val recipients: List<Recipient>,
    val subject: String,
    val content: String?,
    val date: LocalDateTime?,
    val folderId: Int = 0,
    val unread: Boolean?,
    val unreadBy: Int?,
    val readBy: Int?,
    val removed: Boolean,
    val hasAttachments: Boolean
)

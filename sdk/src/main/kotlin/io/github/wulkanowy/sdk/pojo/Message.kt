package io.github.wulkanowy.sdk.pojo

import java.time.LocalDateTime
import java.time.ZonedDateTime

data class Message(
    val id: Int?,
    val messageId: String?,
    val mailbox: String,
    val sender: Sender?,
    val recipients: List<Recipient>,
    val correspondents: String,
    val subject: String,
    val content: String?,
    @Deprecated("use dateZoned instead")
    val date: LocalDateTime?,
    val dateZoned: ZonedDateTime?,
    val folderId: Int = 0,
    val unread: Boolean?,
    val hasAttachments: Boolean
)

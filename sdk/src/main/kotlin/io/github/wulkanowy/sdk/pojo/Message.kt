package io.github.wulkanowy.sdk.pojo

import java.time.ZonedDateTime

data class Message(
    val globalKey: String,
    val id: Int,
    val mailbox: String,
    val recipients: List<Recipient>,
    val correspondents: String,
    val subject: String,
    val content: String?,
    val date: ZonedDateTime,
    val folderId: Int,
    val unread: Boolean,
    val readBy: Int?,
    val unreadBy: Int?,
    val hasAttachments: Boolean,
)

package io.github.wulkanowy.sdk.pojo

import org.threeten.bp.LocalDateTime

data class Message(
    val id: Int?,
    val messageId: Int?,
    val sender: String?,
    val senderId: Int?,
    val recipient: String?,
    val subject: String,
    val content: String?,
    val date: LocalDateTime?,
    val folderId: Int = 0,
    val unread: Boolean?,
    val unreadBy: Int?,
    val readBy: Int?,
    var removed: Boolean = false
)

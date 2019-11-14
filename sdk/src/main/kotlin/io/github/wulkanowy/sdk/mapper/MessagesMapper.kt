package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.normalizeRecipient
import io.github.wulkanowy.sdk.pojo.Message
import io.github.wulkanowy.sdk.toLocalDateTime
import io.github.wulkanowy.sdk.scrapper.messages.Message as ScrapperMessage
import io.github.wulkanowy.sdk.mobile.messages.Message as ApiMessage

@JvmName("mapApiMessages")
fun List<ApiMessage>.mapMessages(): List<Message> {
    return map {
        Message(
            id = it.messageId,
            sender = it.senderName,
            unreadBy = it.unread?.toInt(),
            unread = it.readDateTime == null,
            senderId = it.senderId,
            removed = it.status == "Usunieta",
            recipient = it.recipients?.joinToString(", ") { recipient -> recipient.name.normalizeRecipient() },
            readBy = it.read?.toInt(),
            messageId = it.messageId,
            folderId = when {
                it.folder == "Odebrane" -> 1
                it.folder == "Wyslane" -> 2
                else -> 1
            },
            content = it.content,
            date = it.sentDateTime.toLocalDateTime(),
            subject = it.subject
        )
    }
}

fun List<ScrapperMessage>.mapMessages(): List<Message> {
    return map {
        Message(
            id = it.id,
            subject = it.subject,
            date = it.date?.toLocalDateTime(),
            content = it.content,
            folderId = it.folderId,
            messageId = it.messageId,
            readBy = it.readBy,
            recipient = it.recipient,
            removed = it.removed,
            sender = it.sender,
            senderId = it.senderId,
            unread = it.unread,
            unreadBy = it.unreadBy
        )
    }
}

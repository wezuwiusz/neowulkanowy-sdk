package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.mobile.dictionaries.Dictionaries
import io.github.wulkanowy.sdk.normalizeRecipient
import io.github.wulkanowy.sdk.pojo.Message
import io.github.wulkanowy.sdk.pojo.MessageAttachment
import io.github.wulkanowy.sdk.pojo.MessageDetails
import io.github.wulkanowy.sdk.toLocalDateTime
import io.github.wulkanowy.sdk.mobile.messages.Message as ApiMessage
import io.github.wulkanowy.sdk.scrapper.messages.Message as ScrapperMessage

@JvmName("mapApiMessages")
fun List<ApiMessage>.mapMessages(dictionaries: Dictionaries) = map {
    Message(
        id = it.messageId,
        unreadBy = it.unread?.toInt(),
        unread = it.folder == "Odebrane" && it.readDateTime == null,
        sender = it.senderName ?: dictionaries.employees.singleOrNull { employee -> employee.id == it.senderId }?.let { e -> "${e.name} ${e.surname}" },
        senderId = it.senderId,
        removed = it.status == "Usunieta",
        recipients = it.recipients?.map { recipient -> recipient.copy(name = recipient.name.normalizeRecipient()) }?.mapFromMobileToRecipients().orEmpty(),
        readBy = it.read?.toInt(),
        messageId = it.messageId,
        folderId = when (it.folder) {
            "Odebrane" -> 1
            "Wyslane" -> 2
            else -> 1
        },
        content = it.content,
        date = it.sentDateTime.toLocalDateTime(),
        subject = it.subject,
        hasAttachments = false
    )
}

fun List<ScrapperMessage>.mapMessages() = map {
    Message(
        id = it.id,
        subject = it.subject,
        date = it.date?.toLocalDateTime(),
        content = it.content,
        folderId = it.folderId,
        messageId = it.messageId,
        readBy = it.readBy,
        recipients = it.recipients?.mapRecipients().orEmpty(),
        removed = it.removed,
        sender = it.sender?.name,
        senderId = it.sender?.loginId,
        unread = it.unread,
        unreadBy = it.unreadBy,
        hasAttachments = it.hasAttachments
    )
}

fun ScrapperMessage.mapScrapperMessage() = MessageDetails(
    content = requireNotNull(content),
    attachments = attachments?.map {
        MessageAttachment(
            id = it.id,
            messageId = it.messageId,
            oneDriveId = it.oneDriveId,
            url = it.url,
            filename = it.filename
        )
    }.orEmpty()
)

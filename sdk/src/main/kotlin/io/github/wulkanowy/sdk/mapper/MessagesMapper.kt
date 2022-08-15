package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.mobile.dictionaries.Dictionaries
import io.github.wulkanowy.sdk.normalizeRecipient
import io.github.wulkanowy.sdk.pojo.Folder
import io.github.wulkanowy.sdk.pojo.Message
import io.github.wulkanowy.sdk.pojo.MessageAttachment
import io.github.wulkanowy.sdk.pojo.MessageDetails
import io.github.wulkanowy.sdk.pojo.Sender
import io.github.wulkanowy.sdk.toLocalDateTime
import java.time.ZoneId
import io.github.wulkanowy.sdk.mobile.messages.Message as ApiMessage
import io.github.wulkanowy.sdk.scrapper.messages.Message as ScrapperMessage
import io.github.wulkanowy.sdk.scrapper.messages.MessageMeta as ScrapperMessageMeta

@JvmName("mapApiMessages")
fun List<ApiMessage>.mapMessages(dictionaries: Dictionaries, zoneId: ZoneId) = map {
    Message(
        id = it.messageId,
        unread = it.folder == "Odebrane" && it.readDateTime == null,
        sender = Sender(
            id = null,
            loginId = it.senderId,
            name = it.senderName ?: dictionaries.employees.singleOrNull { employee -> employee.id == it.senderId }?.let { e -> "${e.name} ${e.surname}" },
            reportingUnitId = null,
            hash = null,
            role = 2
        ),
        recipients = it.recipients?.map { recipient -> recipient.copy(name = recipient.name.normalizeRecipient()) }?.mapFromMobileToRecipients().orEmpty(),
        messageId = it.messageId.toString(),
        correspondents = "",
        mailbox = "",
        folderId = when (it.folder) {
            "Odebrane" -> 1
            "Wyslane" -> 2
            else -> 1
        },
        content = it.content,
        date = it.sentDateTime.toLocalDateTime(),
        dateZoned = it.sentDateTime.toLocalDateTime().atZone(zoneId),
        subject = it.subject,
        hasAttachments = false
    )
}

fun List<ScrapperMessageMeta>.mapMessages(zoneId: ZoneId, folderId: Folder) = map {
    Message(
        id = it.id,
        mailbox = it.mailbox,
        subject = it.subject,
        date = it.date,
        dateZoned = it.date.atZone(zoneId),
        content = null,
        folderId = folderId.id,
        messageId = it.apiGlobalKey,
        recipients = emptyList(),
        sender = null,
        correspondents = it.correspondents,
        unread = !it.isRead,
        hasAttachments = it.isAttachments,
    )
}

fun ScrapperMessage.mapScrapperMessage() = MessageDetails(
    content = content,
    apiGlobalKey = apiGlobalKey,
    date = date,
    mailboxId = mailboxId,
    senderMailboxId = senderMailboxId,
    senderMailboxName = senderMailboxName,
    recipients = recipients.mapRecipients(),
    subject = subject,
    id = id,
    attachments = attachments.map {
        MessageAttachment(
            url = it.url,
            filename = it.filename
        )
    },
)

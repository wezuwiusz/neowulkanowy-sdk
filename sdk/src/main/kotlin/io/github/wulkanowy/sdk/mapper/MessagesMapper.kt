package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.Folder
import io.github.wulkanowy.sdk.pojo.MailboxType
import io.github.wulkanowy.sdk.pojo.Message
import io.github.wulkanowy.sdk.pojo.MessageAttachment
import io.github.wulkanowy.sdk.pojo.MessageDetails
import io.github.wulkanowy.sdk.pojo.MessageReplayDetails
import io.github.wulkanowy.sdk.pojo.Recipient
import io.github.wulkanowy.sdk.toLocalDateTime
import java.time.ZoneId
import io.github.wulkanowy.sdk.hebe.models.Message as HebeMessage
import io.github.wulkanowy.sdk.scrapper.messages.MessageDetails as ScrapperDetailsMessage
import io.github.wulkanowy.sdk.scrapper.messages.MessageMeta as ScrapperMessageMeta
import io.github.wulkanowy.sdk.scrapper.messages.MessageReplayDetails as ScrapperReplayDetailsMessage

@JvmName("MapScrapperMessages")
internal fun List<ScrapperMessageMeta>.mapMessages(zoneId: ZoneId, folderId: Folder) = map {
    Message(
        globalKey = it.apiGlobalKey,
        id = it.id.toString(),
        mailbox = it.mailbox,
        subject = it.subject,
        date = it.date.atZone(zoneId),
        content = null,
        folderId = folderId.id,
        recipients = emptyList(),
        correspondents = it.correspondents,
        unread = !it.isRead,
        unreadBy = it.readUnreadBy?.substringBefore("/")?.toIntOrNull(),
        readBy = it.readUnreadBy?.substringAfter("/")?.toIntOrNull(),
        hasAttachments = it.isAttachments,
    )
}

internal fun ScrapperDetailsMessage.mapScrapperMessage() = MessageDetails(
    content = content,
    apiGlobalKey = apiGlobalKey,
    date = date,
    sender = sender,
    recipients = recipients,
    subject = subject,
    id = id,
    attachments = attachments.map {
        MessageAttachment(
            url = it.url,
            filename = it.filename,
        )
    },
)

internal fun ScrapperReplayDetailsMessage.mapScrapperMessage() = MessageReplayDetails(
    content = content,
    apiGlobalKey = apiGlobalKey,
    date = date,
    mailboxId = mailboxId,
    senderMailboxId = senderMailboxId,
    senderMailboxName = senderMailboxName,
    sender = sender.mapToRecipient(),
    recipients = recipients.mapRecipients(),
    subject = subject,
    id = id,
    attachments = attachments.map {
        MessageAttachment(
            url = it.url,
            filename = it.filename,
        )
    },
)

@JvmName("MapHebeMessages")
internal fun List<HebeMessage>.mapMessages(zoneId: ZoneId, folderId: Folder) = map {
    val recipientInfo = it.receiver[0].name.split(" - ")

    Message(
        globalKey = it.id,
        id = it.id,
        mailbox = null,
        subject = it.subject,
        date = it.dateSent.timestamp
            .toLocalDateTime()
            .atZone(zoneId),
        content = it.content,
        folderId = folderId.id,
        recipients = listOf(
            Recipient(
                fullName = recipientInfo[0],
                userName = recipientInfo[0],
                studentName = recipientInfo[0],
                schoolNameShort = recipientInfo[2],
                type = MailboxType.fromLetter(recipientInfo[1]),
                mailboxGlobalKey = it.globalKey,
            ),
        ),
        correspondents = it.sender.name,
        unread = false,
        unreadBy = 0,
        readBy = 0,
        hasAttachments = it.attachments.isNotEmpty(),
    )
}

package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.extractNameFromRecipient
import io.github.wulkanowy.sdk.extractSchoolShortFromRecipient
import io.github.wulkanowy.sdk.extractTypeFromRecipient
import io.github.wulkanowy.sdk.pojo.Folder
import io.github.wulkanowy.sdk.pojo.Message
import io.github.wulkanowy.sdk.pojo.MessageAttachment
import io.github.wulkanowy.sdk.pojo.MessageDetails
import io.github.wulkanowy.sdk.pojo.MessageReplayDetails
import io.github.wulkanowy.sdk.pojo.Recipient
import io.github.wulkanowy.sdk.toLocalDateTime
import java.time.ZoneId
import io.github.wulkanowy.sdk.hebe.models.Message as HebeMessage
import io.github.wulkanowy.sdk.hebe.models.Message.Attachment as HebeAttachment
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

internal fun Array<HebeAttachment>.mapAttachments() = map {
    MessageAttachment(
        filename = it.name,
        url = it.link,
    )
}

@JvmName("MapHebeMessages")
internal fun List<HebeMessage>.mapMessages(zoneId: ZoneId, folderId: Folder) = map {
    val isReceived = it.receiver[0].hasRead == null

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
        recipients = it.receiver.map { recipient ->
            Recipient(
                fullName = recipient.name,
                userName = recipient.name,
                studentName = recipient.name.extractNameFromRecipient(),
                schoolNameShort = recipient.name.extractSchoolShortFromRecipient(),
                type = recipient.name.extractTypeFromRecipient(),
                mailboxGlobalKey = it.globalKey,
            )
        },
        correspondents = it.sender.name,
        unread = (it.dateRead == null) && isReceived,
        unreadBy = when (isReceived) {
            true -> null
            false -> {
                var count = 0
                for (recipient in it.receiver) {
                    if (recipient.hasRead == 0) count++
                }
                count
            }
        },
        readBy = when (isReceived) {
            true -> null
            false -> {
                var count = 0
                for (recipient in it.receiver) {
                    if (recipient.hasRead == 1) count++
                }
                count
            }
        },
        hasAttachments = it.attachments.isNotEmpty(),
        attachments = it.attachments.mapAttachments(),
    )
}

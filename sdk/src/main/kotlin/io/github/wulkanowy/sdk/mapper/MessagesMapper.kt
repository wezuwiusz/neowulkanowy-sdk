package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.Folder
import io.github.wulkanowy.sdk.pojo.Message
import io.github.wulkanowy.sdk.pojo.MessageAttachment
import io.github.wulkanowy.sdk.pojo.MessageDetails
import io.github.wulkanowy.sdk.pojo.MessageReplayDetails
import java.time.ZoneId
import io.github.wulkanowy.sdk.scrapper.messages.MessageDetails as ScrapperDetailsMessage
import io.github.wulkanowy.sdk.scrapper.messages.MessageMeta as ScrapperMessageMeta
import io.github.wulkanowy.sdk.scrapper.messages.MessageReplayDetails as ScrapperReplayDetailsMessage

internal fun List<ScrapperMessageMeta>.mapMessages(zoneId: ZoneId, folderId: Folder) = map {
    Message(
        globalKey = it.apiGlobalKey,
        id = it.id,
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

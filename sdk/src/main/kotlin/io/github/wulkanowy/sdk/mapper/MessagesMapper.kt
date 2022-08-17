package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.normalizeRecipient
import io.github.wulkanowy.sdk.pojo.Folder
import io.github.wulkanowy.sdk.pojo.Message
import io.github.wulkanowy.sdk.pojo.MessageAttachment
import io.github.wulkanowy.sdk.pojo.MessageDetails
import io.github.wulkanowy.sdk.pojo.MessageReplayDetails
import io.github.wulkanowy.sdk.toLocalDateTime
import java.time.ZoneId
import io.github.wulkanowy.sdk.mobile.messages.Message as ApiMessage
import io.github.wulkanowy.sdk.scrapper.messages.MessageReplayDetails as ScrapperReplayDetailsMessage
import io.github.wulkanowy.sdk.scrapper.messages.MessageDetails as ScrapperDetailsMessage
import io.github.wulkanowy.sdk.scrapper.messages.MessageMeta as ScrapperMessageMeta

@JvmName("mapApiMessages")
fun List<ApiMessage>.mapMessages(zoneId: ZoneId) = map {
    Message(
        globalKey = it.messageId.toString(),
        id = it.messageId,
        unread = it.folder == "Odebrane" && it.readDateTime == null,
        recipients = it.recipients?.map { recipient -> recipient.copy(name = recipient.name.normalizeRecipient()) }?.mapFromMobileToRecipients().orEmpty(),
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
        globalKey = it.apiGlobalKey,
        id = it.id,
        mailbox = it.mailbox,
        subject = it.subject,
        date = it.date,
        dateZoned = it.date.atZone(zoneId),
        content = null,
        folderId = folderId.id,
        recipients = emptyList(),
        correspondents = it.correspondents,
        unread = !it.isRead,
        hasAttachments = it.isAttachments,
    )
}

fun ScrapperDetailsMessage.mapScrapperMessage() = MessageDetails(
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
            filename = it.filename
        )
    },
)

fun ScrapperReplayDetailsMessage.mapScrapperMessage() = MessageReplayDetails(
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

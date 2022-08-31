package io.github.wulkanowy.sdk.scrapper.repository

import io.github.wulkanowy.sdk.scrapper.getScriptParam
import io.github.wulkanowy.sdk.scrapper.messages.Mailbox
import io.github.wulkanowy.sdk.scrapper.messages.MessageDetails
import io.github.wulkanowy.sdk.scrapper.messages.MessageMeta
import io.github.wulkanowy.sdk.scrapper.messages.MessageReplayDetails
import io.github.wulkanowy.sdk.scrapper.messages.Recipient
import io.github.wulkanowy.sdk.scrapper.messages.SendMessageRequest
import io.github.wulkanowy.sdk.scrapper.normalizeRecipients
import io.github.wulkanowy.sdk.scrapper.parseName
import io.github.wulkanowy.sdk.scrapper.service.MessagesService
import io.github.wulkanowy.sdk.scrapper.toMailbox
import io.github.wulkanowy.sdk.scrapper.toRecipient
import java.util.UUID

class MessagesRepository(private val api: MessagesService) {

    suspend fun getMailboxes(): List<Mailbox> {
        return api.getMailboxes().map {
            it.toRecipient()
                .parseName()
                .toMailbox()
        }
    }

    suspend fun getRecipients(mailboxKey: String): List<Recipient> {
        return api.getRecipients(mailboxKey).normalizeRecipients()
    }

    suspend fun getReceivedMessages(mailboxKey: String?, lastMessageKey: Int = 0, pageSize: Int = 50): List<MessageMeta> {
        val messages = when (mailboxKey) {
            null -> api.getReceived(lastMessageKey, pageSize)
            else -> api.getReceivedMailbox(mailboxKey, lastMessageKey, pageSize)
        }

        return messages
            .sortedBy { it.date }
            .toList()
    }

    suspend fun getSentMessages(mailboxKey: String?, lastMessageKey: Int = 0, pageSize: Int = 50): List<MessageMeta> {
        val messages = when (mailboxKey) {
            null -> api.getSent(lastMessageKey, pageSize)
            else -> api.getSentMailbox(mailboxKey, lastMessageKey, pageSize)
        }
        return messages
            .sortedBy { it.date }
            .toList()
    }

    suspend fun getDeletedMessages(mailboxKey: String?, lastMessageKey: Int = 0, pageSize: Int = 50): List<MessageMeta> {
        val messages = when (mailboxKey) {
            null -> api.getDeleted(lastMessageKey, pageSize)
            else -> api.getDeletedMailbox(mailboxKey, lastMessageKey, pageSize)
        }
        return messages
            .sortedBy { it.date }
            .toList()
    }

    suspend fun getMessageReplayDetails(globalKey: String): MessageReplayDetails {
        return api.getMessageReplayDetails(globalKey).let {
            it.apply {
                sender = Recipient(
                    mailboxGlobalKey = it.senderMailboxId,
                    fullName = it.senderMailboxName,
                ).parseName()
            }
        }
    }

    suspend fun getMessageDetails(globalKey: String, markAsRead: Boolean): MessageDetails {
        val details = api.getMessageDetails(globalKey)
        if (markAsRead && !details.isRead) {
            runCatching {
                api.markMessageAsRead(mapOf("apiGlobalKey" to globalKey))
            }.getOrNull()
        }
        return details
    }

    suspend fun sendMessage(subject: String, content: String, recipients: List<String>, senderMailboxId: String) {
        val startPage = api.getStart()
        val body = SendMessageRequest(
            globalKey = UUID.randomUUID().toString(),
            threadGlobalKey = UUID.randomUUID().toString(),
            senderMailboxGlobalKey = senderMailboxId,
            recipientsMailboxGlobalKeys = recipients,
            subject = subject,
            content = content,
            attachments = emptyList(),
        )

        api.sendMessage(
            token = getScriptParam("antiForgeryToken", startPage),
            appGuid = getScriptParam("appGuid", startPage),
            appVersion = getScriptParam("version", startPage),
            body = body,
        )
    }

    suspend fun deleteMessages(globalKeys: List<String>, removeForever: Boolean) {
        val startPage = api.getStart()
        val token = getScriptParam("antiForgeryToken", startPage)
        val appGuid = getScriptParam("appGuid", startPage)
        val appVersion = getScriptParam("version", startPage)

        if (!removeForever) {
            api.moveMessageToTrash(
                token = token,
                appGuid = appGuid,
                appVersion = appVersion,
                body = globalKeys,
            )
        } else api.deleteMessage(
            token = token,
            appGuid = appGuid,
            appVersion = appVersion,
            body = globalKeys,
        )
    }
}

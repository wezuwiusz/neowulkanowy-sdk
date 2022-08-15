package io.github.wulkanowy.sdk.scrapper.repository

import io.github.wulkanowy.sdk.scrapper.messages.Mailbox
import io.github.wulkanowy.sdk.scrapper.messages.Message
import io.github.wulkanowy.sdk.scrapper.messages.MessageMeta
import io.github.wulkanowy.sdk.scrapper.messages.Recipient
import io.github.wulkanowy.sdk.scrapper.messages.SendMessageRequest
import io.github.wulkanowy.sdk.scrapper.normalizeRecipients
import io.github.wulkanowy.sdk.scrapper.parseName
import io.github.wulkanowy.sdk.scrapper.service.MessagesService
import io.github.wulkanowy.sdk.scrapper.toMailbox
import io.github.wulkanowy.sdk.scrapper.toRecipient

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

    suspend fun getReceivedMessages(lastMessageKey: Int = 0, pageSize: Int = 50): List<MessageMeta> {
        return api.getReceived(lastMessageKey, pageSize)
            .sortedBy { it.date }
            .toList()
    }

    suspend fun getSentMessages(lastMessageKey: Int = 0, pageSize: Int = 50): List<MessageMeta> {
        return api.getSent(lastMessageKey, pageSize)
            .sortedBy { it.date }
            .toList()
    }

    suspend fun getDeletedMessages(lastMessageKey: Int = 0, pageSize: Int = 50): List<MessageMeta> {
        return api.getDeleted(lastMessageKey, pageSize)
            .sortedBy { it.date }
            .toList()
    }

    suspend fun getMessageRecipients(globalKey: String): List<Recipient> {
        return api.getMessageDetails(globalKey).recipients
    }

    suspend fun getMessageDetails(globalKey: String): Message {
        return api.getMessageDetails(globalKey).let {
            it.copy(recipients = it.recipients.normalizeRecipients())
        }
    }

    suspend fun sendMessage(subject: String, content: String, recipients: List<String>, senderMailboxId: String) {
        val body = SendMessageRequest(
            globalKey = "00000000-0000-0000-0000-000000000000",
            threadGlobalKey = "00000000-0000-0000-0000-000000000000",
            senderMailboxGlobalKey = senderMailboxId,
            recipientsMailboxGlobalKeys = recipients,
            subject = subject,
            content = content,
            attachments = emptyList(),
        )

        return api.sendMessage(body)
    }

    suspend fun deleteMessages(globalKeys: List<String>) {
        return api.deleteMessage(globalKeys)
    }
}

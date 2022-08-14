package io.github.wulkanowy.sdk.scrapper.repository

import io.github.wulkanowy.sdk.scrapper.messages.Mailbox
import io.github.wulkanowy.sdk.scrapper.messages.Message
import io.github.wulkanowy.sdk.scrapper.messages.MessageMeta
import io.github.wulkanowy.sdk.scrapper.messages.Recipient
import io.github.wulkanowy.sdk.scrapper.messages.SendMessageRequest
import io.github.wulkanowy.sdk.scrapper.service.MessagesService
import org.slf4j.LoggerFactory

class MessagesRepository(private val api: MessagesService) {

    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    suspend fun getMailboxes(): List<Mailbox> {
        return api.getMailboxes()
    }

    suspend fun getRecipients(mailboxKey: String): List<Recipient> {
        return api.getRecipients(mailboxKey)
    }

    suspend fun getReceivedMessages(lastMessageKey: Int = 0, pageSize: Int = 50): List<MessageMeta> {
        return api.getReceived(lastMessageKey, pageSize)
            .sortedBy { it.date }
            // .map { it.normalizeRecipients() }
            .toList()
    }

    suspend fun getSentMessages(lastMessageKey: Int = 0, pageSize: Int = 50): List<MessageMeta> {
        return api.getSent(lastMessageKey, pageSize)
            // .map { it.normalizeRecipients() }
            .sortedBy { it.date }
            .toList()
    }

    suspend fun getDeletedMessages(lastMessageKey: Int = 0, pageSize: Int = 50): List<MessageMeta> {
        return api.getDeleted(lastMessageKey, pageSize)
            // .map { it.normalizeRecipients() }
            // .map { it.apply { removed = true } }
            .sortedBy { it.date }
            .toList()
    }

    suspend fun getMessageRecipients(messageId: Int, loginId: Int): List<Recipient> {
        return emptyList()
    }

    suspend fun getMessageDetails(globalKey: String): Message {
        return api.getMessageDetails(globalKey)
    }

    suspend fun sendMessage(subject: String, content: String, recipients: List<String>) {
        val body = SendMessageRequest(
            globalKey = "",
            threadGlobalKey = "",
            senderMailboxGlobalKey = "",
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

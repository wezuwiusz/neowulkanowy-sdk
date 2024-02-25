package io.github.wulkanowy.sdk.scrapper.repository

import io.github.wulkanowy.sdk.scrapper.handleErrors
import io.github.wulkanowy.sdk.scrapper.login.CertificateResponse
import io.github.wulkanowy.sdk.scrapper.login.UrlGenerator
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
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import pl.droidsonroids.jspoon.Jspoon
import java.io.IOException
import java.util.UUID

internal class MessagesRepository(
    private val api: MessagesService,
    private val urlGenerator: UrlGenerator,
) {

    private val certificateAdapter by lazy {
        Jspoon.create().adapter(CertificateResponse::class.java)
    }

    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

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
        if (markAsRead) {
            runCatching {
                loginModule()
                api.markMessageAsRead(mapOf("apiGlobalKey" to globalKey))
            }
                .onFailure { logger.error("Error occur while marking message as read", it) }
                .getOrNull()
        }
        return details
    }

    suspend fun sendMessage(subject: String, content: String, recipients: List<String>, senderMailboxId: String) {
        loginModule()
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
            body = body,
        )
    }

    suspend fun deleteMessages(globalKeys: List<String>, removeForever: Boolean) {
        loginModule()
        when {
            !removeForever -> api.moveMessageToTrash(globalKeys)
            else -> api.deleteMessage(globalKeys)
        }
    }

    suspend fun restoreFromTrash(globalKeys: List<String>) {
        api.restoreFromTrash(globalKeys).handleErrors()
    }

    private suspend fun loginModule() {
        val site = UrlGenerator.Site.MESSAGES
        val startHtml = api.getModuleStart()
        val startDoc = Jsoup.parse(startHtml)

        if ("Working" in startDoc.title()) {
            val cert = certificateAdapter.fromHtml(startHtml)
            val certResponseHtml = api.sendModuleCertificate(
                referer = urlGenerator.createReferer(site),
                url = cert.action,
                certificate = mapOf(
                    "wa" to cert.wa,
                    "wresult" to cert.wresult,
                    "wctx" to cert.wctx,
                ),
            )
            val certResponseDoc = Jsoup.parse(certResponseHtml)
            if ("antiForgeryToken" !in certResponseHtml) {
                throw IOException("Unknown module start page: ${certResponseDoc.title()}")
            } else {
                logger.debug("{} cookies fetch successfully!", site)
            }
        } else {
            logger.debug("{} cookies already fetched!", site)
        }
    }
}

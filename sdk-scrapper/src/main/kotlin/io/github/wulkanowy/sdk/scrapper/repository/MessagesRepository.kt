package io.github.wulkanowy.sdk.scrapper.repository

import io.github.wulkanowy.sdk.scrapper.getScriptParam
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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import pl.droidsonroids.jspoon.Jspoon
import java.util.UUID

internal class MessagesRepository(
    private val api: MessagesService,
    private val urlGenerator: UrlGenerator,
) {

    @Volatile
    private var isCookiesFetched: Boolean = false

    private val cookiesFetchMutex = Mutex()

    private var cachedStart: String = ""

    private val certificateAdapter by lazy {
        Jspoon.create().adapter(CertificateResponse::class.java)
    }

    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    fun clearStartCache() {
        isCookiesFetched = false
        cachedStart = ""
    }

    private suspend fun fetchCookies() {
        if (isCookiesFetched) return

        cookiesFetchMutex.withLock {
            if (isCookiesFetched) return@withLock
            val start = api.getStart()
            cachedStart = start

            if ("Working" !in Jsoup.parse(start).title()) {
                isCookiesFetched = true
                return@withLock
            }

            val cert = certificateAdapter.fromHtml(start)
            cachedStart = api.sendCertificate(
                referer = urlGenerator.createReferer(UrlGenerator.Site.STUDENT),
                url = cert.action,
                certificate = mapOf(
                    "wa" to cert.wa,
                    "wresult" to cert.wresult,
                    "wctx" to cert.wctx,
                ),
            )
            isCookiesFetched = true
        }
    }

    suspend fun getMailboxes(): List<Mailbox> {
        fetchCookies()

        return api.getMailboxes().map {
            it.toRecipient()
                .parseName()
                .toMailbox()
        }
    }

    suspend fun getRecipients(mailboxKey: String): List<Recipient> {
        fetchCookies()

        return api.getRecipients(mailboxKey).normalizeRecipients()
    }

    suspend fun getReceivedMessages(mailboxKey: String?, lastMessageKey: Int = 0, pageSize: Int = 50): List<MessageMeta> {
        fetchCookies()

        val messages = when (mailboxKey) {
            null -> api.getReceived(lastMessageKey, pageSize)
            else -> api.getReceivedMailbox(mailboxKey, lastMessageKey, pageSize)
        }

        return messages
            .sortedBy { it.date }
            .toList()
    }

    suspend fun getSentMessages(mailboxKey: String?, lastMessageKey: Int = 0, pageSize: Int = 50): List<MessageMeta> {
        fetchCookies()

        val messages = when (mailboxKey) {
            null -> api.getSent(lastMessageKey, pageSize)
            else -> api.getSentMailbox(mailboxKey, lastMessageKey, pageSize)
        }
        return messages
            .sortedBy { it.date }
            .toList()
    }

    suspend fun getDeletedMessages(mailboxKey: String?, lastMessageKey: Int = 0, pageSize: Int = 50): List<MessageMeta> {
        fetchCookies()

        val messages = when (mailboxKey) {
            null -> api.getDeleted(lastMessageKey, pageSize)
            else -> api.getDeletedMailbox(mailboxKey, lastMessageKey, pageSize)
        }
        return messages
            .sortedBy { it.date }
            .toList()
    }

    suspend fun getMessageReplayDetails(globalKey: String): MessageReplayDetails {
        fetchCookies()

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
        fetchCookies()

        val details = api.getMessageDetails(globalKey)
        if (markAsRead) {
            runCatching {
                api.markMessageAsRead(
                    token = getScriptParam("antiForgeryToken", cachedStart),
                    appGuid = getScriptParam("appGuid", cachedStart),
                    appVersion = getScriptParam("version", cachedStart),
                    body = mapOf("apiGlobalKey" to globalKey),
                )
            }.onFailure {
                isCookiesFetched = false
                cachedStart = ""
                logger.error("Error occur while marking message as read", it)
            }.getOrNull()
        }
        return details
    }

    suspend fun sendMessage(subject: String, content: String, recipients: List<String>, senderMailboxId: String) {
        fetchCookies()

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
            token = getScriptParam("antiForgeryToken", cachedStart),
            appGuid = getScriptParam("appGuid", cachedStart),
            appVersion = getScriptParam("version", cachedStart),
            body = body,
        )
    }

    suspend fun deleteMessages(globalKeys: List<String>, removeForever: Boolean) {
        fetchCookies()

        val token = getScriptParam("antiForgeryToken", cachedStart)
        val appGuid = getScriptParam("appGuid", cachedStart)
        val appVersion = getScriptParam("version", cachedStart)

        when {
            !removeForever -> api.moveMessageToTrash(
                token = token,
                appGuid = appGuid,
                appVersion = appVersion,
                body = globalKeys,
            )

            else -> api.deleteMessage(
                token = token,
                appGuid = appGuid,
                appVersion = appVersion,
                body = globalKeys,
            )
        }
    }
}

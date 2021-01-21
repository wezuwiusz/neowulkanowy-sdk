package io.github.wulkanowy.sdk.scrapper.repository

import io.github.wulkanowy.sdk.scrapper.exception.ScrapperException
import io.github.wulkanowy.sdk.scrapper.getScriptParam
import io.github.wulkanowy.sdk.scrapper.interceptor.handleErrors
import io.github.wulkanowy.sdk.scrapper.messages.Attachment
import io.github.wulkanowy.sdk.scrapper.messages.DeleteMessageRequest
import io.github.wulkanowy.sdk.scrapper.messages.Message
import io.github.wulkanowy.sdk.scrapper.messages.Recipient
import io.github.wulkanowy.sdk.scrapper.messages.RecipientsRequest
import io.github.wulkanowy.sdk.scrapper.messages.ReportingUnit
import io.github.wulkanowy.sdk.scrapper.messages.SendMessageRequest
import io.github.wulkanowy.sdk.scrapper.messages.SentMessage
import io.github.wulkanowy.sdk.scrapper.service.MessagesService
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MessagesRepository(private val api: MessagesService) {

    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    suspend fun getReportingUnits(): List<ReportingUnit> {
        return api.getUserReportingUnits().data.orEmpty()
    }

    suspend fun getRecipients(unitId: Int, role: Int = 2): List<Recipient> {
        return api.getRecipients(RecipientsRequest(RecipientsRequest.ParamsVo(
            unitId = unitId,
            role = role
        ))).handleErrors().data.orEmpty().map {
            it.copy(shortName = it.name.normalizeRecipient())
        }
    }

    suspend fun getReceivedMessages(startDate: LocalDateTime?, endDate: LocalDateTime?): List<Message> {
        return api.getReceived(startDate.getDate(), endDate.getDate()).handleErrors().data.orEmpty()
            .sortedBy { it.date }
            .map { it.normalizeRecipients() }
            .toList()
    }

    suspend fun getSentMessages(startDate: LocalDateTime?, endDate: LocalDateTime?): List<Message> {
        return api.getSent(startDate.getDate(), endDate.getDate()).handleErrors().data.orEmpty()
            .map { it.normalizeRecipients() }
            .sortedBy { it.date }
            .toList()
    }

    suspend fun getDeletedMessages(startDate: LocalDateTime?, endDate: LocalDateTime?): List<Message> {
        return api.getDeleted(startDate.getDate(), endDate.getDate()).handleErrors().data.orEmpty()
            .map { it.normalizeRecipients() }
            .map { it.apply { removed = true } }
            .sortedBy { it.date }
            .toList()
    }

    suspend fun getMessageRecipients(messageId: Int, loginId: Int): List<Recipient> {
        return (if (0 == loginId) api.getMessageRecipients(messageId).handleErrors()
        else api.getMessageSender(loginId, messageId)).handleErrors().data.orEmpty().map { recipient ->
            recipient.copy(shortName = recipient.name.normalizeRecipient())
        }
    }

    suspend fun getMessage(messageId: Int, folderId: Int, read: Boolean, id: Int?): String {
        return getMessageDetails(messageId, folderId, read, id).content.orEmpty()
    }

    suspend fun getMessageAttachments(messageId: Int, folderId: Int): List<Attachment> {
        return getMessageDetails(messageId, folderId, false, null).attachments.orEmpty()
    }

    suspend fun getMessageDetails(messageId: Int, folderId: Int, read: Boolean, id: Int?): Message {
        return when (folderId) {
            1 -> api.getInboxMessage("$messageId").handleErrors().data!!
            2 -> api.getOutboxMessage("$messageId").handleErrors().data!!
            3 -> api.getTrashboxMessage("$messageId").handleErrors().data!!
            else -> throw IllegalArgumentException("Unknown folder id: $folderId")
        }
    }

    suspend fun sendMessage(subject: String, content: String, recipients: List<Recipient>): SentMessage {
        val res = api.getStart()
        logger.debug("Subject length: ${subject.length}, content length: ${content.length}, recipients number: ${recipients.size}")
        return api.sendMessage(
            SendMessageRequest(
                SendMessageRequest.Incoming(
                    recipients = recipients,
                    subject = subject,
                    content = content
                )
            ),
            getScriptParam("antiForgeryToken", res).ifBlank { throw ScrapperException("Can't find antiForgeryToken property!") },
            getScriptParam("appGuid", res),
            getScriptParam("version", res)
        ).handleErrors().data!!
    }

    suspend fun deleteMessages(messages: List<Int>, folderId: Int): Boolean {
        val startPage = api.getStart()

        val items = DeleteMessageRequest(folder = folderId, messages = messages)
        val antiForgeryToken = getScriptParam("antiForgeryToken", startPage)
        val appGUID = getScriptParam("appGuid", startPage)
        val version = getScriptParam("version", startPage)

        return when (folderId) {
            1 -> api.deleteInboxMessage(items, antiForgeryToken, appGUID, version)
            2 -> api.deleteOutboxMessage(items, antiForgeryToken, appGUID, version)
            3 -> api.deleteTrashMessages(items, antiForgeryToken, appGUID, version)
            else -> throw IllegalArgumentException("Unknown folder id: $folderId")
        }.success
    }

    private fun String.normalizeRecipient(): String {
        return this.substringBeforeLast("-").substringBefore(" [").substringBeforeLast(" (").trim()
    }

    private fun Message.normalizeRecipients() = copy(
        recipients = recipients?.map { it.copy(name = it.name.normalizeRecipient()) }
    )

    private fun LocalDateTime?.getDate(): String {
        return this?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).orEmpty()
    }
}

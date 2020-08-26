package io.github.wulkanowy.sdk.scrapper.repository

import com.google.gson.Gson
import io.github.wulkanowy.sdk.scrapper.ApiResponse
import io.github.wulkanowy.sdk.scrapper.ScrapperException
import io.github.wulkanowy.sdk.scrapper.exception.VulcanException
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MessagesRepository(private val api: MessagesService) {

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
            .map { it.copy(folderId = 1) }
            .sortedBy { it.date }
            .toList()
    }

    suspend fun getSentMessages(startDate: LocalDateTime?, endDate: LocalDateTime?): List<Message> {
        return api.getSent(startDate.getDate(), endDate.getDate()).handleErrors().data.orEmpty()
            .map { message ->
                message.copy(
                    messageId = message.id,
                    folderId = 2,
                    recipient = message.recipient?.split(";")?.joinToString("; ") { it.normalizeRecipient() }
                )
            }
            .sortedBy { it.date }
            .toList()
    }

    suspend fun getDeletedMessages(startDate: LocalDateTime?, endDate: LocalDateTime?): List<Message> {
        return api.getDeleted(startDate.getDate(), endDate.getDate()).handleErrors().data.orEmpty()
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
        return when(folderId) {
            1 -> api.getInboxMessage(messageId, read, id).handleErrors().data!!
            2 -> api.getOutboxMessage(messageId, read, id).handleErrors().data!!
            else -> api.getTrashboxMessage(messageId, read, id).handleErrors().data!! // 3
        }
    }

    suspend fun sendMessage(subject: String, content: String, recipients: List<Recipient>): SentMessage {
        val res = api.getStart()
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

        val items = messages.map { DeleteMessageRequest(it) }
        val antiForgeryToken = getScriptParam("antiForgeryToken", startPage)
        val appGUID = getScriptParam("appGuid", startPage)
        val version = getScriptParam("version", startPage)

        val res = when (folderId) {
            1 -> api.deleteInboxMessage(items, antiForgeryToken, appGUID, version)
            2 -> api.deleteOutboxMessage(items, antiForgeryToken, appGUID, version)
            else -> api.deleteTrashMessages(items, antiForgeryToken, appGUID, version) // 3
        }

        val apiResponse = if (res.isBlank()) throw VulcanException("Unexpected empty response. Message(s) may already be deleted")
        else Gson().fromJson(res, ApiResponse::class.java)

        return apiResponse.success
    }

    private fun String.normalizeRecipient(): String {
        return this.substringBeforeLast("-").substringBefore(" [").substringBeforeLast(" (").trim()
    }

    private fun LocalDateTime?.getDate(): String {
        return this?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).orEmpty()
    }
}

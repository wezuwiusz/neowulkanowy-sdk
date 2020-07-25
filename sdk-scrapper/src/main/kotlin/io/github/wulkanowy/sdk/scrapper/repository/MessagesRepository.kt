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
        return api.getRecipients(unitId, role).handleErrors().data.orEmpty().map {
            it.copy(shortName = it.name.normalizeRecipient())
        }
    }

    suspend fun getReceivedMessages(startDate: LocalDateTime?, endDate: LocalDateTime?): List<Message> {
        return api.getReceived(getDate(startDate), getDate(endDate)).handleErrors().data.orEmpty()
            .map { it.copy(folderId = 1) }
            .sortedBy { it.date }
            .toList()
    }

    suspend fun getSentMessages(startDate: LocalDateTime?, endDate: LocalDateTime?): List<Message> {
        return api.getSent(getDate(startDate), getDate(endDate)).handleErrors().data.orEmpty()
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
        return api.getDeleted(getDate(startDate), getDate(endDate)).handleErrors().data.orEmpty()
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

    suspend fun getMessageDetails(messageId: Int, folderId: Int, read: Boolean, id: Int?): Message {
        return api.getMessage(messageId, folderId, read, id).handleErrors().data!!
    }

    suspend fun getMessage(messageId: Int, folderId: Int, read: Boolean, id: Int?): String {
        return api.getMessage(messageId, folderId, read, id).handleErrors().data?.content.orEmpty()
    }

    suspend fun getMessageAttachments(messageId: Int, folderId: Int): List<Attachment> {
        return api.getMessage(messageId, folderId, false, null).handleErrors().data?.attachments.orEmpty()
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

    suspend fun deleteMessages(messages: List<Pair<Int, Int>>): Boolean {
        val startPage = api.getStart()
        val res = api.deleteMessage(
            messages.map { (messageId, folderId) ->
                DeleteMessageRequest(
                    messageId = messageId,
                    folderId = folderId
                )
            },
            getScriptParam("antiForgeryToken", startPage),
            getScriptParam("appGuid", startPage),
            getScriptParam("version", startPage)
        )

        val apiResponse = if (res.isBlank()) throw VulcanException("Unexpected empty response. Message(s) may already be deleted")
        else Gson().fromJson(res, ApiResponse::class.java)

        return apiResponse.success
    }

    private fun String.normalizeRecipient(): String {
        return this.substringBeforeLast("-").substringBefore(" [").substringBeforeLast(" (").trim()
    }

    private fun getDate(date: LocalDateTime?): String {
        if (date == null) return ""
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    }
}

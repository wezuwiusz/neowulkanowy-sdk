package io.github.wulkanowy.api.repository

import io.github.wulkanowy.api.messages.Incoming
import io.github.wulkanowy.api.messages.Message
import io.github.wulkanowy.api.messages.Recipient
import io.github.wulkanowy.api.messages.ReportingUnit
import io.github.wulkanowy.api.messages.SendMessageRequest
import io.github.wulkanowy.api.messages.SentMessage
import io.github.wulkanowy.api.service.MessagesService
import io.reactivex.Observable
import io.reactivex.Single
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class MessagesRepository(private val api: MessagesService) {

    private lateinit var recipients: List<Recipient>

    private lateinit var reportingUnits: List<ReportingUnit>

    fun getReportingUnits(): Single<List<ReportingUnit>> {
        if (::reportingUnits.isInitialized) return Single.just(reportingUnits)

        return api.getUserReportingUnits().map { it.data }.map { list ->
            list.ifEmpty {
                listOf(ReportingUnit())
            }.apply {
                reportingUnits = this
            }
        }
    }

    fun getRecipients(role: Int = 2): Single<List<Recipient>> {
        if (::recipients.isInitialized) return Single.just(recipients)

        return getReportingUnits().map { it.first() }.flatMap { unit ->
            // invalid unit id produced error
            if (unit.id == 0) return@flatMap Single.just(emptyList<Recipient>())
            api.getRecipients(unit.id, role).map { it.data }.map { list ->
                list.ifEmpty { listOf() }.map {
                    it.copy(name = it.name.normalizeRecipient())
                }.apply {
                    recipients = this
                }
            }
        }
    }

    fun getReceivedMessages(startDate: LocalDateTime?, endDate: LocalDateTime?): Single<List<Message>> {
        return api.getReceived(getDate(startDate), getDate(endDate))
                .map { res ->
                    res.data?.asSequence()
                            ?.map { it.copy(folderId = 1) }
                            ?.sortedBy { it.date }?.toList()
                }
    }

    fun getSentMessages(startDate: LocalDateTime?, endDate: LocalDateTime?): Single<List<Message>> {
        return api.getSent(getDate(startDate), getDate(endDate))
                .map { res -> res.data?.asSequence()?.map { it.copy(folderId = 2) }?.sortedBy { it.date }?.toList() }
                .flatMapObservable { Observable.fromIterable(it) }
                .flatMap { message ->
                    getRecipients().flatMapObservable {
                        Observable.fromArray(message.recipient!!
                                .split("; ")
                                .map { recipient -> recipient.normalizeRecipient() }
                                .replaceWithRecipients(it)
                                .ifEmpty {
                                    listOf(Recipient("0", message.recipient.normalizeRecipient(), 0, 0, 2, "unknown"))
                                })
                    }.map {
                        message.copy(recipient = it.joinToString("; ") { recipient -> recipient.name }, messageId = message.id).apply {
                            recipientId = it[0].loginId
                            recipients = it
                        }
                    }
                }
                .toList()
    }

    fun getDeletedMessages(startDate: LocalDateTime?, endDate: LocalDateTime?): Single<List<Message>> {
        return api.getDeleted(getDate(startDate), getDate(endDate))
                .map { res -> res.data?.map { it.apply { removed = true } }?.sortedBy { it.date } }
    }

    fun getMessage(messageId: Int, folderId: Int, read: Boolean, id: Int?): Single<String> {
        return api.getMessage(messageId, folderId, read, id).map { it.data?.content }
    }

    fun sendMessage(subject: String, content: String, recipients: List<Recipient>): Single<SentMessage> {
        return api.sendMessage(SendMessageRequest(
                Incoming(
                        recipients = recipients,
                        subject = subject,
                        content = content
                )
        )).map { it.data }
    }

    private fun String.normalizeRecipient(): String {
        return this.substringBeforeLast("-").substringBefore(" [").trim()
    }

    private fun getDate(date: LocalDateTime?): String {
        if (date == null) return ""
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    }

    private fun List<String>.replaceWithRecipients(recipients: List<Recipient>): List<Recipient> {
        return map { origin ->
            recipients.filter { recipient ->
                origin == recipient.name
            }.ifEmpty {
                listOf(Recipient("0", origin, 0, 0, 2, "unknown"))
            }
        }.flatten()
    }
}

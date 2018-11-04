package io.github.wulkanowy.api.repository

import io.github.wulkanowy.api.messages.Message
import io.github.wulkanowy.api.messages.Recipient
import io.github.wulkanowy.api.messages.ReportingUnit
import io.github.wulkanowy.api.service.MessagesService
import io.reactivex.Observable
import io.reactivex.Single
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class MessagesRepository(private val userId: Int, private val api: MessagesService) {

    private val reportingUnit by lazy {
        getReportingUnits().map { list -> list.first { it.senderId == userId } }
    }

    private val recipients by lazy {
        getRecipients(2).map { list ->
            list.map {
                Pair(it, it.name.split("[").last().split("]").first())
            }
        }
    }

    fun getReportingUnits(): Single<List<ReportingUnit>> {
        return api.getUserReportingUnits().map { it.data }
    }

    fun getRecipients(role: Int): Single<List<Recipient>> {
        return reportingUnit.map { unit -> api.getRecipients(unit.id, role).map { it.data } }.flatMap { it }
    }

    fun getReceivedMessages(startDate: LocalDate?, endDate: LocalDate?): Single<List<Message>> {
        return api.getReceived(getDate(startDate), getDate(endDate))
                .map { res ->
                    res.data?.asSequence()
                            ?.map { it.copy(folderId = 1).apply { conversationId = it.senderId ?: 0 } }
                            ?.sortedBy { it.date }?.toList()
                }
    }

    fun getSentMessages(startDate: LocalDate?, endDate: LocalDate?): Single<List<Message>> {
        return api.getSent(getDate(startDate), getDate(endDate))
                .map { res -> res.data?.asSequence()?.map { it.copy(folderId = 2) }?.sortedBy { it.date }?.toList() }
                .flatMapObservable { Observable.fromIterable(it) }
                .flatMap { message ->
                    recipients.flatMapObservable {
                        Observable.fromIterable(it.filter { recipient ->
                            recipient.second == message.recipient?.split("[")?.last()?.split("]")?.first()
                        })
                    }.map {
                        message.copy(recipient = it.first.name, messageId = message.id).apply {
                            recipientId = it.first.loginId
                            conversationId = it.first.loginId
                        }
                    }
                }
                .toList()
    }

    fun getDeletedMessages(startDate: LocalDate?, endDate: LocalDate?): Single<List<Message>> {
        return api.getDeleted(getDate(startDate), getDate(endDate))
                .map { res -> res.data?.sortedBy { it.date } }
    }

    fun getMessage(id: Int, folderId: Int): Single<Message> {
        return api.getMessage(id, folderId).map { it.data }
    }

    private fun getDate(date: LocalDate?): String {
        if (date == null) return ""
        return date.atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    }
}

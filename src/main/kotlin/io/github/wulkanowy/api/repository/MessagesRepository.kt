package io.github.wulkanowy.api.repository

import io.github.wulkanowy.api.messages.Message
import io.github.wulkanowy.api.messages.Recipient
import io.github.wulkanowy.api.messages.ReportingUnit
import io.github.wulkanowy.api.service.MessagesService
import io.reactivex.Observable
import io.reactivex.Single
import java.text.SimpleDateFormat
import java.util.*

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

    fun getReceivedMessages(dateStart: Date?, endDate: Date?): Single<List<Message>> {
        return api.getReceived(getDate(dateStart), getDate(endDate))
                .map { res -> res.data?.map { it.apply { it.folderId = 1 } }?.sortedBy { it.date } }
    }

    fun getSentMessages(dateStart: Date?, endDate: Date?): Single<List<Message>> {
        return api.getSent(getDate(dateStart), getDate(endDate))
                .map { res -> res.data?.map { it.apply { it.folderId = 2 } }?.sortedBy { it.date } }
                .flatMapObservable { Observable.fromIterable(it) }
                .flatMap { message ->
                    recipients.flatMapObservable {
                        Observable.fromIterable(it.filter { recipient ->
                            recipient.second == message.recipient.split("[").last().split("]").first()
                        })
                    }.map {
                        message.copy(recipientId = it!!.first.loginId, recipient = it.first.name)
                    }
                }
                .toList()
    }

    fun getDeletedMessages(dateStart: Date?, endDate: Date?): Single<List<Message>> {
        return api.getDeleted(getDate(dateStart), getDate(endDate))
                .map { res -> res.data?.sortedBy { it.date } }
    }

    fun getMessage(id: Int, folderId: Int): Single<Message> {
        return api.getMessage(id, folderId).map { it.data }
    }

    private fun getDate(date: Date?): String {
        if (date == null) return ""
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date)
    }
}

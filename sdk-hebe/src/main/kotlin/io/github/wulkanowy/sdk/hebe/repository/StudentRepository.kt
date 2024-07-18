package io.github.wulkanowy.sdk.hebe.repository

import io.github.wulkanowy.sdk.hebe.ApiRequest
import io.github.wulkanowy.sdk.hebe.getEnvelopeOrThrowError
import io.github.wulkanowy.sdk.hebe.models.Exam
import io.github.wulkanowy.sdk.hebe.models.Grade
import io.github.wulkanowy.sdk.hebe.models.GradeAverage
import io.github.wulkanowy.sdk.hebe.models.GradeSummary
import io.github.wulkanowy.sdk.hebe.models.LuckyNumber
import io.github.wulkanowy.sdk.hebe.models.MarkMessageReadRequest
import io.github.wulkanowy.sdk.hebe.models.Message
import io.github.wulkanowy.sdk.hebe.models.MessageUser
import io.github.wulkanowy.sdk.hebe.models.SendMessageRequest
import io.github.wulkanowy.sdk.hebe.models.SetMessageStatusRequest
import io.github.wulkanowy.sdk.hebe.service.StudentService
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

internal class StudentRepository(
    private val studentService: StudentService,
) {

    suspend fun getGrades(pupilId: Int, periodId: Int): List<Grade> = studentService
        .getGrades(
            createQueryMap(pupilId = pupilId, periodId = periodId),
        ).getEnvelopeOrThrowError()
        .orEmpty()

    suspend fun getGradesSummary(pupilId: Int, periodId: Int): List<GradeSummary> = studentService
        .getGradesSummary(
            createQueryMap(pupilId = pupilId, periodId = periodId),
        ).getEnvelopeOrThrowError()
        .orEmpty()

    suspend fun getGradesAverage(pupilId: Int, periodId: Int): List<GradeAverage> = studentService
        .getGradesAverage(
            createQueryMap(pupilId = pupilId, periodId = periodId),
        ).getEnvelopeOrThrowError()
        .orEmpty()

    suspend fun getExams(pupilId: Int, startDate: LocalDate, endDate: LocalDate): List<Exam> = studentService
        .getExams(
            createQueryMap(pupilId = pupilId, dateFrom = startDate),
        ).getEnvelopeOrThrowError()
        .orEmpty()
        .filter {
            it.deadline.date in startDate..endDate
        }

    suspend fun getTeachers(pupilId: Int, periodId: Int) = studentService
        .getTeachers(
            createQueryMap(pupilId = pupilId, periodId = periodId),
        ).getEnvelopeOrThrowError()
        .orEmpty()

    suspend fun getMessages(messageBoxId: String, folder: Int) = studentService
        .getMessages(
            mapOf(
                "box" to messageBoxId,
                "folder" to folder,
                "lastSyncDate" to "1970-01-01 01:00:00",
                "lastId" to Int.MIN_VALUE,
                "pageSize" to 500,
            ),
        ).getEnvelopeOrThrowError()
        .orEmpty()

    suspend fun getMailboxes() = studentService
        .getMailboxes(mapOf())
        .getEnvelopeOrThrowError()
        .orEmpty()

    suspend fun getRecipients(mailboxKey: String) = studentService
        .getRecipients(
            mapOf(
                "box" to mailboxKey,
            ),
        ).getEnvelopeOrThrowError()
        .orEmpty()

    suspend fun getMeetings(pupilId: Int, startDate: LocalDate) = studentService
        .getMeetings(
            mapOf(
                "pupilId" to pupilId,
                "lastSyncDate" to "1970-01-01 01:00:00",
                "lastId" to Int.MIN_VALUE,
                "pageSize" to 500,
                "from" to startDate.format(DateTimeFormatter.ISO_DATE),
            ),
        ).getEnvelopeOrThrowError()
        .orEmpty()

    suspend fun getSchedule(pupilId: Int, startDate: LocalDate, endDate: LocalDate) = studentService
        .getSchedule(
            createQueryMap(
                pupilId = pupilId,
                dateFrom = startDate,
                dateTo = endDate,
            ),
        ).getEnvelopeOrThrowError()
        .orEmpty()

    suspend fun getTimetableChanges(pupilId: Int, startDate: LocalDate, endDate: LocalDate) = studentService
        .getScheduleChanges(
            createQueryMap(
                pupilId = pupilId,
                dateFrom = startDate,
                dateTo = endDate,
            ),
        ).getEnvelopeOrThrowError()
        .orEmpty()

    suspend fun getVacations(pupilId: Int, startDate: LocalDate, endDate: LocalDate) = studentService
        .getVacations(
            createQueryMap(
                pupilId = pupilId,
                dateFrom = startDate,
                dateTo = endDate,
            ),
        ).getEnvelopeOrThrowError()
        .orEmpty()

    suspend fun getCompletedLessons(pupilId: Int, startDate: LocalDate, endDate: LocalDate) = studentService
        .getCompletedLessons(
            createQueryMap(
                pupilId = pupilId,
                dateFrom = startDate,
                dateTo = endDate,
            ),
        ).getEnvelopeOrThrowError()
        .orEmpty()
        .distinctBy { it.lessonId }

    suspend fun getNotes(pupilId: Int) = studentService
        .getNotes(
            createQueryMap(
                pupilId = pupilId,
            ),
        ).getEnvelopeOrThrowError()
        .orEmpty()

    suspend fun getHomework(pupilId: Int, startDate: LocalDate, endDate: LocalDate) = studentService
        .getHomework(
            createQueryMap(
                pupilId = pupilId,
            ),
        ).getEnvelopeOrThrowError()
        .orEmpty()
        .filter {
            it.deadline.date in startDate..endDate
        }

    suspend fun getLuckyNumber(pupilId: Int, constituentUnitId: Int, day: LocalDate) = studentService
        .getLuckyNumber(
            mapOf(
                "pupilId" to pupilId,
                "day" to day.format(DateTimeFormatter.ISO_DATE),
                "constituentId" to constituentUnitId,
            ),
        ).getEnvelopeOrThrowError()
        ?: LuckyNumber(0, day)

    suspend fun setMessageStatus(pupilId: Int?, boxKey: String, messageKey: String, status: Int) = studentService
        .setStatus(
            ApiRequest(
                envelope = listOf(
                    SetMessageStatusRequest(
                        pupilId = pupilId,
                        boxKey = boxKey,
                        messageKey = messageKey,
                        status = status,
                    ),
                ),
            ),
        ).getEnvelopeOrThrowError()

    suspend fun markMessageRead(pupilId: Int?, boxKey: String, messageKey: String) = studentService
        .markMessageRead(
            ApiRequest(
                envelope = MarkMessageReadRequest(
                    pupilId = pupilId,
                    boxKey = boxKey,
                    messageKey = messageKey
                )
            )
        ).getEnvelopeOrThrowError()

    suspend fun sendMessage(subject: String, content: String, recipients: List<MessageUser>, sender: MessageUser): Message? {
        val threadKey = UUID.randomUUID().toString()
        val senderInitials = sender.name.split(" ")
        return studentService
            .sendMessage(
                ApiRequest(
                    envelope =
                        SendMessageRequest(
                            subject = subject,
                            content = content,
                            status = 1,
                            owner = sender.globalKey,
                            id = UUID.randomUUID().toString(),
                            globalKey = threadKey,
                            threadKey = threadKey,
                            importance = 0,
                            withdrawn = false,
                            attachments = emptyList(),
                            partition = sender.partition,
                            sender = SendMessageRequest.Correspondent(
                                id = "0",
                                partition = sender.partition,
                                owner = sender.globalKey,
                                globalKey = sender.globalKey,
                                name = sender.name,
                                group = "",
                                initials = senderInitials[1].first().toString() + senderInitials[0].first().toString(),
                                hasRead = 0,
                                displayedClass = null,
                            ),
                            receiver = recipients.map {
                                val nameSplit = it.name.split(" - ")
                                val receiverInitials = nameSplit[0].split(" ")
                                SendMessageRequest.Correspondent(
                                    id = sender.globalKey + "-" + it.globalKey,
                                    partition = it.partition,
                                    owner = sender.globalKey,
                                    globalKey = it.globalKey,
                                    name = it.name,
                                    group = nameSplit[1],
                                    initials = receiverInitials[1].first().toString() + receiverInitials[0].first().toString(),
                                    hasRead = 0,
                                    displayedClass = SendMessageRequest.DisplayedClass(
                                        displayedClass = null,
                                    ),
                                )
                            },
                            dateSent = SendMessageRequest.Date(
                                date = LocalDate.now(),
                                dateDisplay = LocalDate.now().toString().replace("-", "."),
                                time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                                timestamp = ZonedDateTime.now(ZoneId.of("GMT")).toInstant().toEpochMilli(),
                            ),
                            dateRead = null,
                        ),
                ),
            ).getEnvelopeOrThrowError()
    }

    private fun createQueryMap(
        pupilId: Int,
        periodId: Int? = null,
        dateFrom: LocalDate? = null,
        dateTo: LocalDate? = null,
    ): Map<String, Any?> = mapOf(
        "pupilId" to pupilId,
        "periodId" to periodId,
        "lastSyncDate" to "1970-01-01 01:00:00",
        "lastId" to Int.MIN_VALUE,
        "pageSize" to 500,
        "dateFrom" to dateFrom?.format(DateTimeFormatter.ISO_DATE),
        "dateTo" to dateTo?.format(DateTimeFormatter.ISO_DATE),
    ).filterValues { it != null }
}

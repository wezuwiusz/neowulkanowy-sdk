package io.github.wulkanowy.sdk.mobile.repository

import io.github.wulkanowy.sdk.mobile.ApiRequest
import io.github.wulkanowy.sdk.mobile.ApiResponse
import io.github.wulkanowy.sdk.mobile.attendance.Attendance
import io.github.wulkanowy.sdk.mobile.attendance.AttendanceRequest
import io.github.wulkanowy.sdk.mobile.dictionaries.Dictionaries
import io.github.wulkanowy.sdk.mobile.dictionaries.DictionariesRequest
import io.github.wulkanowy.sdk.mobile.exams.Exam
import io.github.wulkanowy.sdk.mobile.exams.ExamsRequest
import io.github.wulkanowy.sdk.mobile.grades.Grade
import io.github.wulkanowy.sdk.mobile.grades.GradesRequest
import io.github.wulkanowy.sdk.mobile.grades.GradesSummaryResponse
import io.github.wulkanowy.sdk.mobile.homework.Homework
import io.github.wulkanowy.sdk.mobile.homework.HomeworkRequest
import io.github.wulkanowy.sdk.mobile.messages.Message
import io.github.wulkanowy.sdk.mobile.messages.MessageStatusChangeRequest
import io.github.wulkanowy.sdk.mobile.messages.MessagesRequest
import io.github.wulkanowy.sdk.mobile.messages.Recipient
import io.github.wulkanowy.sdk.mobile.messages.SendMessageRequest
import io.github.wulkanowy.sdk.mobile.notes.Note
import io.github.wulkanowy.sdk.mobile.notes.NotesRequest
import io.github.wulkanowy.sdk.mobile.school.Teacher
import io.github.wulkanowy.sdk.mobile.school.TeachersRequest
import io.github.wulkanowy.sdk.mobile.service.MobileService
import io.github.wulkanowy.sdk.mobile.timetable.Lesson
import io.github.wulkanowy.sdk.mobile.timetable.TimetableRequest
import io.github.wulkanowy.sdk.mobile.toFormat
import java.time.LocalDate
import java.time.LocalDateTime

class MobileRepository(private val api: MobileService) {

    suspend fun logStart(): ApiResponse<String> = api.logAppStart(ApiRequest())

    suspend fun getDictionaries(userId: Int, classificationPeriodId: Int, classId: Int): Dictionaries {
        return api.getDictionaries(DictionariesRequest(userId, classificationPeriodId, classId)).data!!
    }

    suspend fun getTeachers(studentId: Int, semesterId: Int): List<Teacher> {
        return api.getTeachers(TeachersRequest(studentId, semesterId)).data.let {
            it?.schoolTeachers.orEmpty().union(it?.teachersSubjects.orEmpty()).toList()
        }
    }

    suspend fun getTimetable(start: LocalDate, end: LocalDate, classId: Int, classificationPeriodId: Int, studentId: Int): List<Lesson> {
        return api.getTimetable(TimetableRequest(start.toFormat(), end.toFormat(), classId, classificationPeriodId, studentId)).data!!
    }

    suspend fun getGradesDetails(classId: Int, classificationPeriodId: Int, studentId: Int): List<Grade> {
        return api.getGrades(GradesRequest(classId, classificationPeriodId, studentId)).data!!
    }

    suspend fun getGradesSummary(classId: Int, classificationPeriodId: Int, studentId: Int): GradesSummaryResponse {
        return api.getGradesSummary(GradesRequest(classId, classificationPeriodId, studentId)).data!!
    }

    suspend fun getExams(start: LocalDate, end: LocalDate, classId: Int, classificationPeriodId: Int, studentId: Int): List<Exam> {
        return api.getExams(ExamsRequest(start.toFormat(), end.toFormat(), classId, classificationPeriodId, studentId)).data!!
    }

    suspend fun getNotes(classificationPeriodId: Int, studentId: Int): List<Note> {
        return api.getNotes(NotesRequest(classificationPeriodId, studentId)).data!!
    }

    suspend fun getAttendance(start: LocalDate, end: LocalDate, classId: Int, classificationPeriodId: Int, studentId: Int): List<Attendance> {
        return api.getAttendance(AttendanceRequest(start.toFormat(), end.toFormat(), classId, classificationPeriodId, studentId)).data!!.items
    }

    suspend fun getHomework(start: LocalDate, end: LocalDate, classId: Int, classificationPeriodId: Int, studentId: Int): List<Homework> {
        return api.getHomework(HomeworkRequest(start.toFormat(), end.toFormat(), classId, classificationPeriodId, studentId)).data!!
    }

    suspend fun getMessages(start: LocalDateTime, end: LocalDateTime, loginId: Int, studentId: Int): List<Message> {
        return api.getMessages(MessagesRequest(start.toFormat(), end.toFormat(), loginId, studentId)).data!!
    }

    suspend fun getMessagesDeleted(start: LocalDateTime, end: LocalDateTime, loginId: Int, studentId: Int): List<Message> {
        return api.getMessagesDeleted(MessagesRequest(start.toFormat(), end.toFormat(), loginId, studentId)).data!!
    }

    suspend fun getMessagesSent(start: LocalDateTime, end: LocalDateTime, loginId: Int, studentId: Int): List<Message> {
        return api.getMessagesSent(MessagesRequest(start.toFormat(), end.toFormat(), loginId, studentId)).data!!
    }

    suspend fun changeMessageStatus(messageId: Int, folder: String, status: String, loginId: Int, studentId: Int): String {
        return api.changeMessageStatus(MessageStatusChangeRequest(messageId, folder, status, loginId, studentId)).data!!
    }

    suspend fun sendMessage(sender: String, subject: String, content: String, recipients: List<Recipient>, loginId: Int, studentId: Int): Message {
        val request = SendMessageRequest(
            sender = sender,
            subject = subject,
            content = content,
            recipients = recipients,
            loginId = loginId,
            studentId = studentId
        )
        return api.sendMessage(request)
    }
}

package io.github.wulkanowy.sdk.repository

import io.github.wulkanowy.sdk.ApiRequest
import io.github.wulkanowy.sdk.ApiResponse
import io.github.wulkanowy.sdk.attendance.Attendance
import io.github.wulkanowy.sdk.attendance.AttendanceRequest
import io.github.wulkanowy.sdk.dictionaries.Dictionaries
import io.github.wulkanowy.sdk.dictionaries.DictionariesRequest
import io.github.wulkanowy.sdk.exams.Exam
import io.github.wulkanowy.sdk.exams.ExamsRequest
import io.github.wulkanowy.sdk.grades.Grade
import io.github.wulkanowy.sdk.grades.GradesRequest
import io.github.wulkanowy.sdk.grades.GradesSummaryResponse
import io.github.wulkanowy.sdk.homework.Homework
import io.github.wulkanowy.sdk.homework.HomeworkRequest
import io.github.wulkanowy.sdk.messages.Message
import io.github.wulkanowy.sdk.messages.MessageStatusChangeRequest
import io.github.wulkanowy.sdk.messages.MessagesRequest
import io.github.wulkanowy.sdk.notes.Note
import io.github.wulkanowy.sdk.notes.NotesRequest
import io.github.wulkanowy.sdk.school.Teacher
import io.github.wulkanowy.sdk.school.TeachersRequest
import io.github.wulkanowy.sdk.service.MobileService
import io.github.wulkanowy.sdk.timetable.Lesson
import io.github.wulkanowy.sdk.timetable.TimetableRequest
import io.github.wulkanowy.sdk.toFormat
import io.reactivex.Single
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

class MobileRepository(private val api: MobileService) {

    fun logStart(): Single<ApiResponse<String>> = api.logAppStart(object : ApiRequest() {})

    fun getDictionaries(userId: Int, classificationPeriodId: Int, classId: Int): Single<Dictionaries> {
        return api.getDictionaries(DictionariesRequest(userId, classificationPeriodId, classId)).map { it.data }
    }

    fun getTeachers(studentId: Int, semesterId: Int): Single<List<Teacher>> {
        return api.getTeachers(TeachersRequest(studentId, semesterId)).map { it.data }.map {
            it?.schoolTeachers?.union(it.teachersSubjects)?.toList()
        }
    }

    fun getTimetable(start: LocalDate, end: LocalDate, classId: Int, classificationPeriodId: Int, studentId: Int): Single<List<Lesson>> {
        return api.getTimetable(TimetableRequest(start.toFormat(), end.toFormat(), classId, classificationPeriodId, studentId)).map { it.data }
    }

    fun getGrades(classId: Int, classificationPeriodId: Int, studentId: Int): Single<List<Grade>> {
        return api.getGrades(GradesRequest(classId, classificationPeriodId, studentId)).map { it.data }
    }

    fun getGradesSummary(classId: Int, classificationPeriodId: Int, studentId: Int): Single<GradesSummaryResponse> {
        return api.getGradesSummary(GradesRequest(classId, classificationPeriodId, studentId)).map { it.data }
    }

    fun getExams(start: LocalDate, end: LocalDate, classId: Int, classificationPeriodId: Int, studentId: Int): Single<List<Exam>> {
        return api.getExams(ExamsRequest(start.toFormat(), end.toFormat(), classId, classificationPeriodId, studentId)).map { it.data }
    }

    fun getNotes(classificationPeriodId: Int, studentId: Int): Single<List<Note>> {
        return api.getNotes(NotesRequest(classificationPeriodId, studentId)).map { it.data }
    }

    fun getAttendance(start: LocalDate, end: LocalDate, classId: Int, classificationPeriodId: Int, studentId: Int): Single<List<Attendance>> {
        return api.getAttendance(AttendanceRequest(start.toFormat(), end.toFormat(), classId, classificationPeriodId, studentId)).map { it.data?.data }
    }

    fun getHomework(start: LocalDate, end: LocalDate, classId: Int, classificationPeriodId: Int, studentId: Int): Single<List<Homework>> {
        return api.getHomework(HomeworkRequest(start.toFormat(), end.toFormat(), classId, classificationPeriodId, studentId)).map { it.data }
    }

    fun getMessages(start: LocalDateTime, end: LocalDateTime, loginId: Int, studentId: Int): Single<List<Message>> {
        return api.getMessages(MessagesRequest(start.toFormat(), end.toFormat(), loginId, studentId)).map { it.data }
    }

    fun getMessagesDeleted(start: LocalDateTime, end: LocalDateTime, loginId: Int, studentId: Int): Single<List<Message>> {
        return api.getMessagesDeleted(MessagesRequest(start.toFormat(), end.toFormat(), loginId, studentId)).map { it.data }
    }

    fun getMessagesSent(start: LocalDateTime, end: LocalDateTime, loginId: Int, studentId: Int): Single<List<Message>> {
        return api.getMessagesSent(MessagesRequest(start.toFormat(), end.toFormat(), loginId, studentId)).map { it.data }
    }

    fun changeMessageStatus(messageId: Int, folder: String, status: String, loginId: Int, studentId: Int): Single<String> {
        return api.changeMessageStatus(MessageStatusChangeRequest(messageId, folder, status, loginId, studentId)).map { it.data }
    }
}

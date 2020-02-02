package io.github.wulkanowy.sdk.mobile.service

import io.github.wulkanowy.sdk.mobile.ApiRequest
import io.github.wulkanowy.sdk.mobile.ApiResponse
import io.github.wulkanowy.sdk.mobile.attendance.AttendanceRequest
import io.github.wulkanowy.sdk.mobile.attendance.AttendanceResponse
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
import io.github.wulkanowy.sdk.mobile.messages.SendMessageRequest
import io.github.wulkanowy.sdk.mobile.notes.Note
import io.github.wulkanowy.sdk.mobile.notes.NotesRequest
import io.github.wulkanowy.sdk.mobile.school.TeachersRequest
import io.github.wulkanowy.sdk.mobile.school.TeachersResponse
import io.github.wulkanowy.sdk.mobile.timetable.Lesson
import io.github.wulkanowy.sdk.mobile.timetable.TimetableRequest
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface MobileService {

    @POST("LogAppStart")
    fun logAppStart(@Body logAppStartRequest: ApiRequest): Single<ApiResponse<String>>

    @POST("Slowniki")
    fun getDictionaries(@Body dictionariesRequest: DictionariesRequest): Single<ApiResponse<Dictionaries>>

    @POST("Nauczyciele")
    fun getTeachers(@Body teachersRequest: TeachersRequest): Single<ApiResponse<TeachersResponse>>

    @POST("PlanLekcjiZeZmianami")
    fun getTimetable(@Body timetableRequest: TimetableRequest): Single<ApiResponse<List<Lesson>>>

    @POST("Oceny")
    fun getGrades(@Body gradesRequest: GradesRequest): Single<ApiResponse<List<Grade>>>

    @POST("OcenyPodsumowanie")
    fun getGradesSummary(@Body gradesRequest: GradesRequest): Single<ApiResponse<GradesSummaryResponse>>

    @POST("Sprawdziany")
    fun getExams(@Body examsRequest: ExamsRequest): Single<ApiResponse<List<Exam>>>

    @POST("UwagiUcznia")
    fun getNotes(@Body notesRequest: NotesRequest): Single<ApiResponse<List<Note>>>

    @POST("Frekwencje")
    fun getAttendance(@Body attendanceRequest: AttendanceRequest): Single<ApiResponse<AttendanceResponse>>

    @POST("ZadaniaDomowe")
    fun getHomework(@Body homeworkRequest: HomeworkRequest): Single<ApiResponse<List<Homework>>>

    @POST("WiadomosciOdebrane")
    fun getMessages(@Body messagesRequest: MessagesRequest): Single<ApiResponse<List<Message>>>

    @POST("WiadomosciWyslane")
    fun getMessagesSent(@Body messagesRequest: MessagesRequest): Single<ApiResponse<List<Message>>>

    @POST("WiadomosciUsuniete")
    fun getMessagesDeleted(@Body messagesRequest: MessagesRequest): Single<ApiResponse<List<Message>>>

    @POST(".")
    fun changeMessageStatus(@Body messageStatusChangeRequest: MessageStatusChangeRequest): Single<ApiResponse<String>>

    @POST("DodajWiadomosc")
    fun sendMessage(@Body sendMessageRequest: SendMessageRequest): Single<Message>
}

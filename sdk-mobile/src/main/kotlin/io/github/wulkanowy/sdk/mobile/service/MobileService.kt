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
import retrofit2.http.Body
import retrofit2.http.POST

interface MobileService {

    @POST("LogAppStart")
    suspend fun logAppStart(@Body logAppStartRequest: ApiRequest): ApiResponse<String>

    @POST("Slowniki")
    suspend fun getDictionaries(@Body dictionariesRequest: DictionariesRequest): ApiResponse<Dictionaries>

    @POST("Nauczyciele")
    suspend fun getTeachers(@Body teachersRequest: TeachersRequest): ApiResponse<TeachersResponse>

    @POST("PlanLekcjiZeZmianami")
    suspend fun getTimetable(@Body timetableRequest: TimetableRequest): ApiResponse<List<Lesson>>

    @POST("Oceny")
    suspend fun getGrades(@Body gradesRequest: GradesRequest): ApiResponse<List<Grade>>

    @POST("OcenyPodsumowanie")
    suspend fun getGradesSummary(@Body gradesRequest: GradesRequest): ApiResponse<GradesSummaryResponse>

    @POST("Sprawdziany")
    suspend fun getExams(@Body examsRequest: ExamsRequest): ApiResponse<List<Exam>>

    @POST("UwagiUcznia")
    suspend fun getNotes(@Body notesRequest: NotesRequest): ApiResponse<List<Note>>

    @POST("Frekwencje")
    suspend fun getAttendance(@Body attendanceRequest: AttendanceRequest): ApiResponse<AttendanceResponse>

    @POST("ZadaniaDomowe")
    suspend fun getHomework(@Body homeworkRequest: HomeworkRequest): ApiResponse<List<Homework>>

    @POST("WiadomosciOdebrane")
    suspend fun getMessages(@Body messagesRequest: MessagesRequest): ApiResponse<List<Message>>

    @POST("WiadomosciWyslane")
    suspend fun getMessagesSent(@Body messagesRequest: MessagesRequest): ApiResponse<List<Message>>

    @POST("WiadomosciUsuniete")
    suspend fun getMessagesDeleted(@Body messagesRequest: MessagesRequest): ApiResponse<List<Message>>

    @POST("ZmienStatusWiadomosci")
    suspend fun changeMessageStatus(@Body messageStatusChangeRequest: MessageStatusChangeRequest): ApiResponse<String>

    @POST("DodajWiadomosc")
    suspend fun sendMessage(@Body sendMessageRequest: SendMessageRequest): Message
}

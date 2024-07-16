package io.github.wulkanowy.sdk.hebe.service

import io.github.wulkanowy.sdk.hebe.ApiRequest
import io.github.wulkanowy.sdk.hebe.ApiResponse
import io.github.wulkanowy.sdk.hebe.models.CompletedLesson
import io.github.wulkanowy.sdk.hebe.models.Exam
import io.github.wulkanowy.sdk.hebe.models.Grade
import io.github.wulkanowy.sdk.hebe.models.GradeAverage
import io.github.wulkanowy.sdk.hebe.models.GradeSummary
import io.github.wulkanowy.sdk.hebe.models.Homework
import io.github.wulkanowy.sdk.hebe.models.Lesson
import io.github.wulkanowy.sdk.hebe.models.LuckyNumber
import io.github.wulkanowy.sdk.hebe.models.Mailbox
import io.github.wulkanowy.sdk.hebe.models.Meeting
import io.github.wulkanowy.sdk.hebe.models.Message
import io.github.wulkanowy.sdk.hebe.models.Note
import io.github.wulkanowy.sdk.hebe.models.Recipient
import io.github.wulkanowy.sdk.hebe.models.SendMessageRequest
import io.github.wulkanowy.sdk.hebe.models.SetMessageStatusRequest
import io.github.wulkanowy.sdk.hebe.models.Teacher
import io.github.wulkanowy.sdk.hebe.models.TimetableChange
import io.github.wulkanowy.sdk.hebe.models.Vacation
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap

@JvmSuppressWildcards
internal interface StudentService {

    @GET("api/mobile/grade/byPupil")
    suspend fun getGrades(@QueryMap query: Map<String, Any?>): ApiResponse<List<Grade>>

    @GET("api/mobile/grade/summary/byPupil")
    suspend fun getGradesSummary(@QueryMap query: Map<String, Any?>): ApiResponse<List<GradeSummary>>

    @GET("api/mobile/grade/average/byPupil")
    suspend fun getGradesAverage(@QueryMap query: Map<String, Any?>): ApiResponse<List<GradeAverage>>

    @GET("api/mobile/exam/byPupil")
    suspend fun getExams(@QueryMap query: Map<String, Any?>): ApiResponse<List<Exam>>

    @GET("api/mobile/teacher/byPeriod")
    suspend fun getTeachers(@QueryMap query: Map<String, Any?>): ApiResponse<List<Teacher>>

    @GET("api/mobile/messages/byBox")
    suspend fun getMessages(@QueryMap query: Map<String, Any?>): ApiResponse<List<Message>>

    @GET("api/mobile/messagebox")
    suspend fun getMailboxes(@QueryMap query: Map<String, Any?>): ApiResponse<List<Mailbox>>

    @GET("api/mobile/addressbook")
    suspend fun getRecipients(@QueryMap query: Map<String, Any?>): ApiResponse<List<Recipient>>

    @GET("api/mobile/meetings/byPupil")
    suspend fun getMeetings(@QueryMap query: Map<String, Any?>): ApiResponse<List<Meeting>>

    @GET("api/mobile/schedule/byPupil")
    suspend fun getSchedule(@QueryMap query: Map<String, Any?>): ApiResponse<List<Lesson>>

    @GET("api/mobile/schedule/changes/byPupil")
    suspend fun getScheduleChanges(@QueryMap query: Map<String, Any?>): ApiResponse<List<TimetableChange>>

    @GET("api/mobile/school/vacation")
    suspend fun getVacations(@QueryMap query: Map<String, Any?>): ApiResponse<List<Vacation>>

    @GET("api/mobile/lesson/byPupil")
    suspend fun getCompletedLessons(@QueryMap query: Map<String, Any?>): ApiResponse<List<CompletedLesson>>

    @GET("api/mobile/note/byPupil")
    suspend fun getNotes(@QueryMap query: Map<String, Any?>): ApiResponse<List<Note>>

    @GET("api/mobile/homework/byPupil")
    suspend fun getHomework(@QueryMap query: Map<String, Any?>): ApiResponse<List<Homework>>

    @GET("api/mobile/school/lucky")
    suspend fun getLuckyNumber(@QueryMap query: Map<String, Any?>): ApiResponse<LuckyNumber>

    @POST("api/mobile/messages/statuses")
    suspend fun setStatus(@Body request: ApiRequest<List<SetMessageStatusRequest>>): ApiResponse<Boolean>

    @POST("api/mobile/messages")
    suspend fun sendMessage(@Body request: ApiRequest<SendMessageRequest>): ApiResponse<Message>
}

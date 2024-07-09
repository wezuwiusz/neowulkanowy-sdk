package io.github.wulkanowy.sdk.hebe.service

import io.github.wulkanowy.sdk.hebe.ApiRequest
import io.github.wulkanowy.sdk.hebe.ApiResponse
import io.github.wulkanowy.sdk.hebe.models.Exam
import io.github.wulkanowy.sdk.hebe.models.Grade
import io.github.wulkanowy.sdk.hebe.models.GradeAverage
import io.github.wulkanowy.sdk.hebe.models.GradeSummary
import io.github.wulkanowy.sdk.hebe.models.Mailbox
import io.github.wulkanowy.sdk.hebe.models.Message
import io.github.wulkanowy.sdk.hebe.models.SetMessageStatusRequest
import io.github.wulkanowy.sdk.hebe.models.Teacher
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

    @POST("api/mobile/messages/statuses")
    suspend fun setStatus(@Body request: ApiRequest<List<SetMessageStatusRequest>>): ApiResponse<Boolean>
}

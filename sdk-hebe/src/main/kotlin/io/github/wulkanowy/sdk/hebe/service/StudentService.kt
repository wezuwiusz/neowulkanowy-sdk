package io.github.wulkanowy.sdk.hebe.service

import io.github.wulkanowy.sdk.hebe.ApiResponse
import io.github.wulkanowy.sdk.hebe.models.Exam
import io.github.wulkanowy.sdk.hebe.models.Grade
import io.github.wulkanowy.sdk.hebe.models.GradeAverage
import io.github.wulkanowy.sdk.hebe.models.GradeSummary
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

@JvmSuppressWildcards
internal interface StudentService {

    @GET("api/mobile/grade/byPupil")
    suspend fun getGrades(@QueryMap query: Map<String, Any?>): ApiResponse<List<Grade>>

    @GET("api/mobile/grade/summary/byPupil")
    suspend fun getGradesSummary(@QueryMap query: Map<String, Any?> ): ApiResponse<List<GradeSummary>>

    @GET("api/mobile/grade/average/byPupil")
    suspend fun getGradesAverage(@QueryMap query: Map<String, Any?>): ApiResponse<List<GradeAverage>>

    @GET("api/mobile/exam/byPupil")
    suspend fun getExams(@QueryMap query: Map<String, Any?>): ApiResponse<List<Exam>>
}

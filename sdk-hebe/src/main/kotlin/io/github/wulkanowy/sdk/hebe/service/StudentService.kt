package io.github.wulkanowy.sdk.hebe.service

import io.github.wulkanowy.sdk.hebe.ApiResponse
import io.github.wulkanowy.sdk.hebe.models.Grade
import retrofit2.http.GET
import retrofit2.http.Query

internal interface StudentService {

    @GET("api/mobile/grade/byPupil")
    suspend fun getGrades(
        @Query("pupilId") pupilId: Int,
        @Query("periodId") periodId: Int,
        @Query("lastSyncDate") lastSyncDate: String = "1970-01-01 01:00:00",
        @Query("lastId") lastId: Int = Int.MIN_VALUE,
        @Query("pageSize") pageSize: Int = 500,
    ): ApiResponse<List<Grade>>
}

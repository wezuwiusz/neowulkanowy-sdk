package io.github.wulkanowy.sdk.scrapper.service

import io.github.wulkanowy.sdk.scrapper.ApiResponse
import io.github.wulkanowy.sdk.scrapper.home.GovernmentUnit
import io.github.wulkanowy.sdk.scrapper.home.HomepageTileResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

internal interface HomepageService {

    @GET("Start.mvc")
    suspend fun getStart(): String

    @FormUrlEncoded
    @POST("Start.mvc/GetStudentDirectorInformations")
    suspend fun getDirectorInformation(@Field("permissions") token: String): ApiResponse<List<HomepageTileResponse>>

    @FormUrlEncoded
    @POST("Start.mvc/GetSelfGovernments")
    suspend fun getSelfGovernments(@Field("permissions") token: String): ApiResponse<List<GovernmentUnit>>

    @FormUrlEncoded
    @POST("Start.mvc/GetStudentTrips")
    suspend fun getStudentsTrips(@Field("permissions") token: String): ApiResponse<List<HomepageTileResponse>>

    @FormUrlEncoded
    @POST("Start.mvc/GetStudentThreats")
    suspend fun getStudentThreats(@Field("permissions") token: String): ApiResponse<List<HomepageTileResponse>>

    @FormUrlEncoded
    @POST("Start.mvc/GetLastNotes")
    suspend fun getLastGrades(@Field("permissions") token: String): ApiResponse<List<HomepageTileResponse>>

    @FormUrlEncoded
    @POST("Start.mvc/GetFreeDays")
    suspend fun getFreeDays(@Field("permissions") token: String): ApiResponse<List<HomepageTileResponse>>

    @FormUrlEncoded
    @POST("Start.mvc/GetKidsLuckyNumbers")
    suspend fun getKidsLuckyNumbers(@Field("permissions") token: String): ApiResponse<List<HomepageTileResponse>>

    @FormUrlEncoded
    @POST("Start.mvc/GetKidsLessonPlan")
    suspend fun getKidsLessonPlan(@Field("permissions") token: String): ApiResponse<List<HomepageTileResponse>>

    @FormUrlEncoded
    @POST("Start.mvc/GetLastHomeworks")
    suspend fun getLastHomework(@Field("permissions") token: String): ApiResponse<List<HomepageTileResponse>>

    @FormUrlEncoded
    @POST("Start.mvc/GetLastTests")
    suspend fun getLastTests(@Field("permissions") token: String): ApiResponse<List<HomepageTileResponse>>

    @FormUrlEncoded
    @POST("Start.mvc/GetLastStudentLessons")
    suspend fun getLastStudentLessons(@Field("permissions") token: String): ApiResponse<List<HomepageTileResponse>>
}

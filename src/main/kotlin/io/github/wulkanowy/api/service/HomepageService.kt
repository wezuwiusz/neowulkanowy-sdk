package io.github.wulkanowy.api.service

import io.github.wulkanowy.api.ApiResponse
import io.github.wulkanowy.api.home.HomepageTileResponse
import io.reactivex.Single
import retrofit2.http.GET

interface HomepageService {

    @GET("Start.mvc/GetSelfGovernments")
    fun getSelfGovernments(): Single<ApiResponse<List<HomepageTileResponse>>>

    @GET("Start.mvc/GetStudentTrips")
    fun getStudentsTrips(): Single<ApiResponse<List<HomepageTileResponse>>>

    @GET("Start.mvc/GetLastNotes")
    fun getLastGrades(): Single<ApiResponse<List<HomepageTileResponse>>>

    @GET("Start.mvc/GetFreeDays")
    fun getFreeDays(): Single<ApiResponse<List<HomepageTileResponse>>>

    @GET("Start.mvc/GetKidsLuckyNumbers")
    fun getKidsLuckyNumbers(): Single<ApiResponse<List<HomepageTileResponse>>>

    @GET("Start.mvc/GetKidsLessonPlan")
    fun getKidsLessonPlan(): Single<ApiResponse<List<HomepageTileResponse>>>

    @GET("Start.mvc/GetLastHomeworks")
    fun getLastHomework(): Single<ApiResponse<List<HomepageTileResponse>>>

    @GET("Start.mvc/GetLastTests")
    fun getLastTests(): Single<ApiResponse<List<HomepageTileResponse>>>

    @GET("Start.mvc/GetLastStudentLessons")
    fun getLastStudentLessons(): Single<ApiResponse<List<HomepageTileResponse>>>
}

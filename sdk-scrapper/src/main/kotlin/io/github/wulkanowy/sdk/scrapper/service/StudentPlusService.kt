package io.github.wulkanowy.sdk.scrapper.service

import io.github.wulkanowy.sdk.scrapper.attendance.Attendance
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceExcusePlusRequest
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceExcusesPlusResponse
import io.github.wulkanowy.sdk.scrapper.conferences.Conference
import io.github.wulkanowy.sdk.scrapper.grades.GradeSemester
import io.github.wulkanowy.sdk.scrapper.mobile.Device
import io.github.wulkanowy.sdk.scrapper.mobile.TokenResponse
import io.github.wulkanowy.sdk.scrapper.register.AuthorizePermissionPlusRequest
import io.github.wulkanowy.sdk.scrapper.register.ContextResponse
import io.github.wulkanowy.sdk.scrapper.timetable.CompletedLesson
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

internal interface StudentPlusService {

    @GET("api/Context")
    suspend fun getContext(): ContextResponse

    @GET("api/OkresyKlasyfikacyjne")
    suspend fun getSemesters(
        @Query("key") key: String,
        @Query("idDziennik") diaryId: Int,
    ): List<GradeSemester>

    @POST("api/AutoryzacjaPesel")
    suspend fun authorize(@Body body: AuthorizePermissionPlusRequest): Response<Unit>

    @GET("api/Frekwencja")
    suspend fun getAttendance(
        @Query("key") key: String,
        @Query("dataOd") from: String,
        @Query("dataDo") to: String,
    ): List<Attendance>

    @GET("api/Usprawiedliwienia")
    suspend fun getExcuses(
        @Query("key") key: String,
        @Query("dataOd") from: String,
        @Query("dataDo") to: String,
    ): AttendanceExcusesPlusResponse

    @POST("api/Usprawiedliwienia")
    suspend fun excuseForAbsence(@Body body: AttendanceExcusePlusRequest): Response<Unit>

    @GET("api/ZarejestrowaneUrzadzenia")
    suspend fun getRegisteredDevices(): List<Device>

    @GET("api/RejestracjaUrzadzeniaToken")
    suspend fun getDeviceRegistrationToken(): TokenResponse

    @GET("api/Zebrania")
    suspend fun getConferences(): List<Conference>

    @GET("api/RealizacjaZajec")
    suspend fun getCompletedLessons(
        @Query("key") key: String,
        @Query("status") status: Int,
        @Query("dataOd") from: String,
        @Query("dataDo") to: String,
    ): List<CompletedLesson>

    @GET("api/Oceny")
    suspend fun getGrades(
        @Query("key") key: String,
        @Query("idOkresKlasyfikacyjny") semesterId: Int,
    ): GradesResponse
}

package io.github.wulkanowy.sdk.scrapper.service

import io.github.wulkanowy.sdk.scrapper.ApiEndpoints
import io.github.wulkanowy.sdk.scrapper.attendance.Attendance
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceExcusePlusRequest
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceExcusesPlusResponse
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceSummaryResponse
import io.github.wulkanowy.sdk.scrapper.conferences.Conference
import io.github.wulkanowy.sdk.scrapper.exams.ExamDetailsPlus
import io.github.wulkanowy.sdk.scrapper.grades.GradeSemester
import io.github.wulkanowy.sdk.scrapper.grades.GradesResponse
import io.github.wulkanowy.sdk.scrapper.homework.ExamHomeworkPlus
import io.github.wulkanowy.sdk.scrapper.homework.HomeworkDetailsPlus
import io.github.wulkanowy.sdk.scrapper.mobile.Device
import io.github.wulkanowy.sdk.scrapper.mobile.TokenResponse
import io.github.wulkanowy.sdk.scrapper.notes.Note
import io.github.wulkanowy.sdk.scrapper.register.AuthorizePermissionPlusRequest
import io.github.wulkanowy.sdk.scrapper.register.ContextResponse
import io.github.wulkanowy.sdk.scrapper.school.SchoolPlus
import io.github.wulkanowy.sdk.scrapper.school.TeacherPlusResponse
import io.github.wulkanowy.sdk.scrapper.student.StudentInfo
import io.github.wulkanowy.sdk.scrapper.student.StudentPhoto
import io.github.wulkanowy.sdk.scrapper.timetable.CompletedLesson
import io.github.wulkanowy.sdk.scrapper.timetable.LessonPlus
import io.github.wulkanowy.sdk.scrapper.timetable.TimetablePlusHeader
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

internal interface StudentPlusService {

    // for register

    @GET
    suspend fun getContextByUrl(@Url url: String): ContextResponse

    @GET
    suspend fun getSemestersByUrl(
        @Url url: String,
        @Query("key") key: String,
        @Query("idDziennik") diaryId: Int,
    ): List<GradeSemester>

    //

    @GET("api/{path}")
    suspend fun getContext(@Path("path") path: String = ApiEndpoints.PlusContext): ContextResponse

    @GET("api/{path}")
    suspend fun getSemesters(
        @Path("path") path: String = ApiEndpoints.PlusOkresyKlasyfikacyjne,
        @Query("key") key: String,
        @Query("idDziennik") diaryId: Int,
    ): List<GradeSemester>

    @POST("api/{path}")
    suspend fun authorize(
        @Path("path") path: String = ApiEndpoints.PlusAutoryzacjaPesel,
        @Body body: AuthorizePermissionPlusRequest,
    ): Response<Unit>

    @GET("api/{path}")
    suspend fun getAttendance(
        @Path("path") path: String = ApiEndpoints.PlusFrekwencja,
        @Query("key") key: String,
        @Query("dataOd") from: String,
        @Query("dataDo") to: String,
    ): List<Attendance>

    @GET("api/{path}")
    suspend fun getExcuses(
        @Path("path") path: String = ApiEndpoints.PlusUsprawiedliwienia,
        @Query("key") key: String,
        @Query("dataOd") from: String,
        @Query("dataDo") to: String,
    ): AttendanceExcusesPlusResponse

    @POST("api/{path}")
    suspend fun excuseForAbsence(
        @Path("path") path: String = ApiEndpoints.PlusUsprawiedliwienia,
        @Body body: AttendanceExcusePlusRequest,
    ): Response<Unit>

    @GET("api/{path}")
    suspend fun getAttendanceSummary(
        @Path("path") path: String = ApiEndpoints.PlusFrekwencjaStatystyki,
        @Query("key") key: String,
    ): AttendanceSummaryResponse

    @GET("api/{path}")
    suspend fun getRegisteredDevices(
        @Path("path") path: String = ApiEndpoints.PlusZarejestrowaneUrzadzenia,
        @Query("key") key: String,
    ): List<Device>

    @POST("api/{path}")
    suspend fun createDeviceRegistrationToken(
        @Path("path") path: String = ApiEndpoints.PlusRejestracjaUrzadzeniaToken,
        @Body body: Map<String, String>,
    )

    @GET("api/{path}")
    suspend fun getDeviceRegistrationToken(
        @Path("path") path: String = ApiEndpoints.PlusRejestracjaUrzadzeniaToken,
        @Query("key") key: String,
    ): TokenResponse

    @GET("api/{path}")
    suspend fun getConferences(
        @Path("path") path: String = ApiEndpoints.PlusZebrania,
        @Query("key") key: String,
    ): List<Conference>

    @GET("api/{path}")
    suspend fun getCompletedLessons(
        @Path("path") path: String = ApiEndpoints.PlusRealizacjaZajec,
        @Query("key") key: String,
        @Query("status") status: Int,
        @Query("dataOd") from: String,
        @Query("dataDo") to: String,
    ): List<CompletedLesson>

    @GET("api/{path}")
    suspend fun getGrades(
        @Path("path") path: String = ApiEndpoints.PlusOceny,
        @Query("key") key: String,
        @Query("idOkresKlasyfikacyjny") semesterId: Int,
    ): GradesResponse

    @GET("api/{path}")
    suspend fun getExamsAndHomework(
        @Path("path") path: String = ApiEndpoints.PlusSprawdzianyZadaniaDomowe,
        @Query("key") key: String,
        @Query("dataOd") from: String,
        @Query("dataDo") to: String?,
    ): List<ExamHomeworkPlus>

    @GET("api/{path}")
    suspend fun getExamDetails(
        @Path("path") path: String = ApiEndpoints.PlusSprawdzianSzczegoly,
        @Query("key") key: String,
        @Query("id") id: Int,
    ): ExamDetailsPlus

    @GET("api/{path}")
    suspend fun getHomeworkDetails(
        @Path("path") path: String = ApiEndpoints.PlusZadanieDomoweSzczegoly,
        @Query("key") key: String,
        @Query("id") id: Int,
    ): HomeworkDetailsPlus

    @GET("api/{path}")
    suspend fun getTimetable(
        @Path("path") path: String = ApiEndpoints.PlusPlanZajec,
        @Query("key") key: String,
        @Query("dataOd") from: String,
        @Query("dataDo") to: String?,
        @Query("zakresDanych") data: Int = 2,
    ): List<LessonPlus>

    @GET("api/{path}")
    suspend fun getTimetableFreeDays(
        @Path("path") path: String = ApiEndpoints.PlusDniWolne,
        @Query("key") key: String,
        @Query("dataOd") from: String,
        @Query("dataDo") to: String?,
    ): List<TimetablePlusHeader>

    @GET("api/{path}")
    suspend fun getNotes(
        @Path("path") path: String = ApiEndpoints.PlusUwagi,
        @Query("key") key: String,
    ): List<Note>

    @GET("api/{path}")
    suspend fun getTeachers(
        @Path("path") path: String = ApiEndpoints.PlusNauczyciele,
        @Query("key") key: String,
    ): TeacherPlusResponse

    @GET("api/{path}")
    suspend fun getSchool(
        @Path("path") path: String = ApiEndpoints.PlusInformacje,
        @Query("key") key: String,
    ): SchoolPlus

    @GET("api/{path}")
    suspend fun getStudentInfo(
        @Path("path") path: String = ApiEndpoints.PlusDaneUcznia,
        @Query("key") key: String,
    ): StudentInfo

    @GET("api/{path}")
    suspend fun getStudentPhoto(
        @Path("path") path: String = ApiEndpoints.PlusUczenZdjecie,
        @Query("key") key: String,
    ): StudentPhoto?
}

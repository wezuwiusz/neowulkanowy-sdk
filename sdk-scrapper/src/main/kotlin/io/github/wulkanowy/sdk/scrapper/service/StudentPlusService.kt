package io.github.wulkanowy.sdk.scrapper.service

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
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

internal interface StudentPlusService {

    // for register

    @GET
    suspend fun getContextByUrl(
        @HeaderMap vHeaders: Map<String, String>,
        @Url url: String,
    ): ContextResponse

    @GET
    suspend fun getSemestersByUrl(
        @HeaderMap vHeaders: Map<String, String>,
        @Url url: String,
        @Query("key") key: String,
        @Query("idDziennik") diaryId: Int,
    ): List<GradeSemester>

    //

    @GET("api/Context")
    suspend fun getContext(): ContextResponse

    @GET("api/OkresyKlasyfikacyjne")
    suspend fun getSemesters(
        @Query("key") key: String,
        @Query("idDziennik") diaryId: Int,
    ): List<GradeSemester>

    @POST("api/AutoryzacjaPesel")
    suspend fun authorize(
        @Body body: AuthorizePermissionPlusRequest,
    ): Response<Unit>

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
    suspend fun excuseForAbsence(
        @Body body: AttendanceExcusePlusRequest,
    ): Response<Unit>

    @GET("api/FrekwencjaStatystyki")
    suspend fun getAttendanceSummary(
        @Query("key") key: String,
    ): AttendanceSummaryResponse

    @GET("api/ZarejestrowaneUrzadzenia")
    suspend fun getRegisteredDevices(
        @Query("key") key: String,
    ): List<Device>

    @POST("api/RejestracjaUrzadzeniaToken")
    suspend fun createDeviceRegistrationToken(
        @Body body: Map<String, String>,
    )

    @GET("api/RejestracjaUrzadzeniaToken")
    suspend fun getDeviceRegistrationToken(
        @Query("key") key: String,
    ): TokenResponse

    @GET("api/Zebrania")
    suspend fun getConferences(
        @Query("key") key: String,
    ): List<Conference>

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

    @GET("api/SprawdzianyZadaniaDomowe")
    suspend fun getExamsAndHomework(
        @Query("key") key: String,
        @Query("dataOd") from: String,
        @Query("dataDo") to: String?,
    ): List<ExamHomeworkPlus>

    @GET("api/SprawdzianSzczegoly")
    suspend fun getExamDetails(
        @Query("key") key: String,
        @Query("id") id: Int,
    ): ExamDetailsPlus

    @GET("api/ZadanieDomoweSzczegoly")
    suspend fun getHomeworkDetails(
        @Query("key") key: String,
        @Query("id") id: Int,
    ): HomeworkDetailsPlus

    @GET("api/PlanZajec")
    suspend fun getTimetable(
        @Query("key") key: String,
        @Query("dataOd") from: String,
        @Query("dataDo") to: String?,
        @Query("zakresDanych") data: Int = 2,
    ): List<LessonPlus>

    @GET("api/DniWolne")
    suspend fun getTimetableFreeDays(
        @Query("key") key: String,
        @Query("dataOd") from: String,
        @Query("dataDo") to: String?,
    ): List<TimetablePlusHeader>

    @GET("api/Uwagi")
    suspend fun getNotes(
        @Query("key") key: String,
    ): List<Note>

    @GET("api/Nauczyciele")
    suspend fun getTeachers(
        @Query("key") key: String,
    ): TeacherPlusResponse

    @GET("api/Informacje")
    suspend fun getSchool(
        @Query("key") key: String,
    ): SchoolPlus

    @GET("api/DaneUcznia")
    suspend fun getStudentInfo(
        @Query("key") key: String,
    ): StudentInfo

    @GET("api/UczenZdjecie")
    suspend fun getStudentPhoto(
        @Query("key") key: String,
    ): StudentPhoto?
}

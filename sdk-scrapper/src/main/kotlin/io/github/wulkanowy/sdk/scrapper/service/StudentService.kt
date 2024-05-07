package io.github.wulkanowy.sdk.scrapper.service

import io.github.wulkanowy.sdk.scrapper.ApiEndpoints
import io.github.wulkanowy.sdk.scrapper.ApiResponse
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceExcuseRequest
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceRecordDay
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceRecordsRequest
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceRequest
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceResponse
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceSummaryRequest
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceSummaryResponse
import io.github.wulkanowy.sdk.scrapper.attendance.Subject
import io.github.wulkanowy.sdk.scrapper.conferences.Conference
import io.github.wulkanowy.sdk.scrapper.exams.ExamRequest
import io.github.wulkanowy.sdk.scrapper.exams.ExamResponse
import io.github.wulkanowy.sdk.scrapper.grades.GradePointsSummaryResponse
import io.github.wulkanowy.sdk.scrapper.grades.GradeRequest
import io.github.wulkanowy.sdk.scrapper.grades.GradesResponse
import io.github.wulkanowy.sdk.scrapper.grades.GradesStatisticsPartial
import io.github.wulkanowy.sdk.scrapper.grades.GradesStatisticsRequest
import io.github.wulkanowy.sdk.scrapper.grades.GradesStatisticsSemester
import io.github.wulkanowy.sdk.scrapper.homework.HomeworkDay
import io.github.wulkanowy.sdk.scrapper.homework.HomeworkRequest
import io.github.wulkanowy.sdk.scrapper.menu.Menu
import io.github.wulkanowy.sdk.scrapper.menu.MenuRequest
import io.github.wulkanowy.sdk.scrapper.mobile.Device
import io.github.wulkanowy.sdk.scrapper.mobile.TokenResponse
import io.github.wulkanowy.sdk.scrapper.mobile.UnregisterDeviceRequest
import io.github.wulkanowy.sdk.scrapper.notes.NotesResponse
import io.github.wulkanowy.sdk.scrapper.register.AuthorizePermissionRequest
import io.github.wulkanowy.sdk.scrapper.register.AuthorizePermissionResponse
import io.github.wulkanowy.sdk.scrapper.register.Diary
import io.github.wulkanowy.sdk.scrapper.school.SchoolAndTeachersResponse
import io.github.wulkanowy.sdk.scrapper.student.StudentInfo
import io.github.wulkanowy.sdk.scrapper.student.StudentPhoto
import io.github.wulkanowy.sdk.scrapper.timetable.CacheResponse
import io.github.wulkanowy.sdk.scrapper.timetable.CompletedLesson
import io.github.wulkanowy.sdk.scrapper.timetable.CompletedLessonsRequest
import io.github.wulkanowy.sdk.scrapper.timetable.TimetableRequest
import io.github.wulkanowy.sdk.scrapper.timetable.TimetableResponse
import retrofit2.http.Body
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Url

internal interface StudentService {

    @GET
    suspend fun getStart(@Url url: String): String

    @GET("LoginEndpoint.aspx")
    suspend fun getModuleStart(): String

    @POST
    @FormUrlEncoded
    suspend fun sendModuleCertificate(
        @Header("Referer") referer: String,
        @Url url: String,
        @FieldMap certificate: Map<String, String>,
    ): String

    @POST
    suspend fun getUserCache(
        @Url url: String,
        @Header("X-V-RequestVerificationToken") token: String,
        @Header("X-V-AppGuid") appGuid: String,
        @Header("X-V-AppVersion") appVersion: String,
        @Body body: Any = Any(),
    ): ApiResponse<CacheResponse>

    @POST("{path}.mvc/Get")
    suspend fun getUserCache(
        @Body body: Any = Any(),
        @Path("path") path: String = ApiEndpoints.UczenCache,
    ): ApiResponse<CacheResponse>

    @POST("{path}.mvc/Post")
    suspend fun authorizePermission(
        @Body body: AuthorizePermissionRequest,
        @Path("path") path: String = ApiEndpoints.Autoryzacja,
    ): ApiResponse<AuthorizePermissionResponse>

    @POST
    suspend fun getSchoolInfo(@Url url: String, @Body body: Any = Any()): ApiResponse<List<Diary>>

    @POST("{path}.mvc/Get")
    suspend fun getDiaries(
        @Body body: Any = Any(),
        @Path("path") path: String = ApiEndpoints.UczenDziennik,
    ): ApiResponse<List<Diary>>

    @POST("{path}.mvc/Get")
    suspend fun getGrades(
        @Body gradeRequest: GradeRequest,
        @Path("path") path: String = ApiEndpoints.Oceny,
    ): ApiResponse<GradesResponse>

    @POST("{path}.mvc/GetOcenyCzastkowe")
    suspend fun getGradesPartialStatistics(
        @Body gradesStatisticsRequest: GradesStatisticsRequest,
        @Path("path") path: String = ApiEndpoints.Statystyki,
    ): ApiResponse<List<GradesStatisticsPartial>>

    @POST("{path}.mvc/GetPunkty")
    suspend fun getGradesPointsStatistics(
        @Body gradesStatisticsRequest: GradesStatisticsRequest,
        @Path("path") path: String = ApiEndpoints.Statystyki,
    ): ApiResponse<GradePointsSummaryResponse>

    @POST("{path}.mvc/GetOcenyRoczne")
    suspend fun getGradesAnnualStatistics(
        @Body gradesStatisticsRequest: GradesStatisticsRequest,
        @Path("path") path: String = ApiEndpoints.Statystyki,
    ): ApiResponse<List<GradesStatisticsSemester>>

    @POST("{path}.mvc/Get")
    suspend fun getAttendance(
        @Body attendanceRequest: AttendanceRequest,
        @Path("path") path: String = ApiEndpoints.Frekwencja,
    ): ApiResponse<AttendanceResponse>

    @POST("{path}.mvc/Get")
    suspend fun getAttendanceStatistics(
        @Body attendanceSummaryRequest: AttendanceSummaryRequest,
        @Path("path") path: String = ApiEndpoints.FrekwencjaStatystyki,
    ): ApiResponse<AttendanceSummaryResponse>

    @POST("{path}.mvc/Get")
    suspend fun getAttendanceSubjects(
        @Body body: Any = Any(),
        @Path("path") path: String = ApiEndpoints.FrekwencjaStatystykiPrzedmioty,
    ): ApiResponse<List<Subject>>

    @POST("{path}.mvc/Get")
    suspend fun getAttendanceRecords(
        @Body attendanceRecordsRequest: AttendanceRecordsRequest,
        @Path("path") path: String = ApiEndpoints.EwidencjaObecnosci,
    ): ApiResponse<List<AttendanceRecordDay>>

    @POST("{path}.mvc/Post")
    suspend fun excuseForAbsence(
        @Body attendanceExcuseRequest: AttendanceExcuseRequest,
        @Path("path") path: String = ApiEndpoints.Usprawiedliwienia,
    ): ApiResponse<ApiResponse<String?>>

    @POST("{path}.mvc/Get")
    suspend fun getExternalExaminations(
        @Path("path") path: String = ApiEndpoints.EgzaminyZewnetrzne,
    )

    @POST("{path}.mvc/Get")
    suspend fun getExams(
        @Body examRequest: ExamRequest,
        @Path("path") path: String = ApiEndpoints.Sprawdziany,
    ): ApiResponse<List<ExamResponse>>

    @POST("{path}.mvc/Get")
    suspend fun getHomework(
        @Body homeworkRequest: HomeworkRequest,
        @Path("path") path: String = ApiEndpoints.Homework,
    ): ApiResponse<List<HomeworkDay>>

    @POST("{path}.mvc/Get")
    suspend fun getTimetable(
        @Body timetableRequest: TimetableRequest,
        @Path("path") path: String = ApiEndpoints.PlanZajec,
    ): ApiResponse<TimetableResponse>

    @POST("{path}.mvc/GetPrzedmioty")
    suspend fun getRealizedSubjects(
        @Body body: Any = Any(),
        @Path("path") path: String = ApiEndpoints.LekcjeZrealizowane,
    )

    @POST("{path}.mvc/GetZrealizowane")
    suspend fun getCompletedLessons(
        @Body completedLessonsRequest: CompletedLessonsRequest,
        @Path("path") path: String = ApiEndpoints.LekcjeZrealizowane,
    ): ApiResponse<Map<String, List<CompletedLesson>>>

    @POST("{path}.mvc/Get")
    suspend fun getNotes(
        @Body body: Any = Any(),
        @Path("path") path: String = ApiEndpoints.UwagiIOsiagniecia,
    ): ApiResponse<NotesResponse>

    @POST("{path}.mvc/Get")
    suspend fun getConferences(
        @Path("path") path: String = ApiEndpoints.Zebrania,
    ): ApiResponse<List<Conference>>

    @POST("{path}.mvc/Get")
    suspend fun getMenu(
        @Body menuRequest: MenuRequest,
        @Path("path") path: String = ApiEndpoints.Jadlospis,
    ): ApiResponse<List<Menu>>

    @POST("{path}.mvc/Get")
    suspend fun getRegisteredDevices(
        @Body body: Any = Any(),
        @Path("path") path: String = ApiEndpoints.ZarejestrowaneUrzadzenia,
    ): ApiResponse<List<Device>>

    @POST("{path}.mvc/Get")
    suspend fun getToken(
        @Body body: Any = Any(),
        @Path("path") path: String = ApiEndpoints.ZarejestrowaneUrzadzenia,
    ): ApiResponse<TokenResponse>

    @POST("{path}.mvc/Delete")
    suspend fun unregisterDevice(
        @Body unregisterDeviceRequest: UnregisterDeviceRequest,
        @Path("path") path: String = ApiEndpoints.ZarejestrowaneUrzadzenia,
    ): ApiResponse<Any>

    @POST("{path}.mvc/Get")
    suspend fun getSchoolAndTeachers(
        @Body body: Any = Any(),
        @Path("path") path: String = ApiEndpoints.SzkolaINauczyciele,
    ): ApiResponse<SchoolAndTeachersResponse>

    @POST("{path}.mvc/Get")
    suspend fun getStudentInfo(
        @Path("path") path: String = ApiEndpoints.Uczen,
    ): ApiResponse<StudentInfo>

    @POST("{path}.mvc/Get")
    suspend fun getStudentPhoto(
        @Path("path") path: String = ApiEndpoints.UczenZdjecie,
    ): ApiResponse<StudentPhoto>
}

package io.github.wulkanowy.sdk.scrapper.service

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
import io.github.wulkanowy.sdk.scrapper.menu.MenuRequest
import io.github.wulkanowy.sdk.scrapper.menu.Menu
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
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

internal interface StudentService {

    @GET
    suspend fun getStart(@Url url: String): String

    @POST
    suspend fun getUserCache(
        @Url url: String,
        @Header("X-V-RequestVerificationToken") token: String,
        @Header("X-V-AppGuid") appGuid: String,
        @Header("X-V-AppVersion") appVersion: String,
        @Body body: Any = Any(),
    ): ApiResponse<CacheResponse>

    @POST("UczenCache.mvc/Get")
    suspend fun getUserCache(
        @Header("X-V-RequestVerificationToken") token: String,
        @Header("X-V-AppGuid") appGuid: String,
        @Header("X-V-AppVersion") appVersion: String,
        @Body body: Any = Any(),
    ): ApiResponse<CacheResponse>

    @POST("Autoryzacja.mvc/Post")
    suspend fun authorizePermission(@Body authorizePermissionRequest: AuthorizePermissionRequest): ApiResponse<AuthorizePermissionResponse>

    @POST
    suspend fun getSchoolInfo(@Url url: String, @Body body: Any = Any()): ApiResponse<List<Diary>>

    @POST("UczenDziennik.mvc/Get")
    suspend fun getDiaries(@Body body: Any = Any()): ApiResponse<List<Diary>>

    @POST("Oceny.mvc/Get")
    suspend fun getGrades(@Body gradeRequest: GradeRequest): ApiResponse<GradesResponse>

    @POST("Statystyki.mvc/GetOcenyCzastkowe")
    suspend fun getGradesPartialStatistics(@Body gradesStatisticsRequest: GradesStatisticsRequest): ApiResponse<List<GradesStatisticsPartial>>

    @POST("Statystyki.mvc/GetPunkty")
    suspend fun getGradesPointsStatistics(@Body gradesStatisticsRequest: GradesStatisticsRequest): ApiResponse<GradePointsSummaryResponse>

    @POST("Statystyki.mvc/GetOcenyRoczne")
    suspend fun getGradesAnnualStatistics(@Body gradesStatisticsRequest: GradesStatisticsRequest): ApiResponse<List<GradesStatisticsSemester>>

    @POST("Frekwencja.mvc/Get")
    suspend fun getAttendance(@Body attendanceRequest: AttendanceRequest): ApiResponse<AttendanceResponse>

    @POST("FrekwencjaStatystyki.mvc/Get")
    suspend fun getAttendanceStatistics(@Body attendanceSummaryRequest: AttendanceSummaryRequest): ApiResponse<AttendanceSummaryResponse>

    @POST("FrekwencjaStatystykiPrzedmioty.mvc/Get")
    suspend fun getAttendanceSubjects(@Body body: Any = Any()): ApiResponse<List<Subject>>

    @POST("EwidencjaObecnosci.mvc/Get")
    suspend fun getAttendanceRecords(@Body attendanceRecordsRequest: AttendanceRecordsRequest): ApiResponse<List<AttendanceRecordDay>>

    @POST("Usprawiedliwienia.mvc/Post")
    suspend fun excuseForAbsence(
        @Header("X-V-RequestVerificationToken") token: String,
        @Header("X-V-AppGuid") appGuid: String,
        @Header("X-V-AppVersion") appVersion: String,
        @Body attendanceExcuseRequest: AttendanceExcuseRequest,
    ): ApiResponse<ApiResponse<String?>>

    @POST("EgzaminyZewnetrzne.mvc/Get")
    suspend fun getExternalExaminations()

    @POST("Sprawdziany.mvc/Get")
    suspend fun getExams(@Body examRequest: ExamRequest): ApiResponse<List<ExamResponse>>

    @POST("Homework.mvc/Get")
    suspend fun getHomework(@Body homeworkRequest: HomeworkRequest): ApiResponse<List<HomeworkDay>>

    @POST("PlanZajec.mvc/Get")
    suspend fun getTimetable(@Body timetableRequest: TimetableRequest): ApiResponse<TimetableResponse>

    @POST("LekcjeZrealizowane.mvc/GetPrzedmioty")
    suspend fun getRealizedSubjects(@Body body: Any = Any())

    @POST("LekcjeZrealizowane.mvc/GetZrealizowane")
    suspend fun getCompletedLessons(@Body completedLessonsRequest: CompletedLessonsRequest): ApiResponse<Map<String, List<CompletedLesson>>>

    @POST("UwagiIOsiagniecia.mvc/Get")
    suspend fun getNotes(@Body body: Any = Any()): ApiResponse<NotesResponse>

    @POST("Zebrania.mvc/Get")
    suspend fun getConferences(): ApiResponse<List<Conference>>

    @POST("Jadlospis.mvc/Get")
    suspend fun getMenu(@Body menuRequest: MenuRequest): ApiResponse<List<Menu>>

    @POST("ZarejestrowaneUrzadzenia.mvc/Get")
    suspend fun getRegisteredDevices(@Body body: Any = Any()): ApiResponse<List<Device>>

    @POST("RejestracjaUrzadzeniaToken.mvc/Get")
    suspend fun getToken(@Body body: Any = Any()): ApiResponse<TokenResponse>

    @POST("ZarejestrowaneUrzadzenia.mvc/Delete")
    suspend fun unregisterDevice(
        @Header("X-V-RequestVerificationToken") token: String,
        @Header("X-V-AppGuid") appGuid: String,
        @Header("X-V-AppVersion") appVersion: String,
        @Body unregisterDeviceRequest: UnregisterDeviceRequest,
    ): ApiResponse<Any>

    @POST("SzkolaINauczyciele.mvc/Get")
    suspend fun getSchoolAndTeachers(@Body body: Any = Any()): ApiResponse<SchoolAndTeachersResponse>

    @POST("Uczen.mvc/Get")
    suspend fun getStudentInfo(): ApiResponse<StudentInfo>

    @POST("UczenZdjecie.mvc/Get")
    suspend fun getStudentPhoto(): ApiResponse<StudentPhoto>
}

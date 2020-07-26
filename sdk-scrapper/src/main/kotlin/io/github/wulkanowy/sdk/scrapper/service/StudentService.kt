package io.github.wulkanowy.sdk.scrapper.service

import io.github.wulkanowy.sdk.scrapper.ApiResponse
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceExcuseRequest
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceRequest
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceResponse
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceSummaryRequest
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceSummaryResponse
import io.github.wulkanowy.sdk.scrapper.attendance.Subject
import io.github.wulkanowy.sdk.scrapper.exams.ExamRequest
import io.github.wulkanowy.sdk.scrapper.exams.ExamResponse
import io.github.wulkanowy.sdk.scrapper.grades.GradePointsSummary
import io.github.wulkanowy.sdk.scrapper.grades.GradeRequest
import io.github.wulkanowy.sdk.scrapper.grades.GradesResponse
import io.github.wulkanowy.sdk.scrapper.grades.GradesStatisticsAnnual
import io.github.wulkanowy.sdk.scrapper.grades.GradesStatisticsPartial
import io.github.wulkanowy.sdk.scrapper.grades.GradesStatisticsRequest
import io.github.wulkanowy.sdk.scrapper.homework.HomeworkDay
import io.github.wulkanowy.sdk.scrapper.homework.HomeworkRequest
import io.github.wulkanowy.sdk.scrapper.homework.HomeworkResponse
import io.github.wulkanowy.sdk.scrapper.mobile.Device
import io.github.wulkanowy.sdk.scrapper.mobile.TokenResponse
import io.github.wulkanowy.sdk.scrapper.mobile.UnregisterDeviceRequest
import io.github.wulkanowy.sdk.scrapper.notes.NotesResponse
import io.github.wulkanowy.sdk.scrapper.register.Diary
import io.github.wulkanowy.sdk.scrapper.school.SchoolAndTeachersResponse
import io.github.wulkanowy.sdk.scrapper.timetable.CacheResponse
import io.github.wulkanowy.sdk.scrapper.timetable.CompletedLessonsRequest
import io.github.wulkanowy.sdk.scrapper.timetable.TimetableRequest
import io.github.wulkanowy.sdk.scrapper.timetable.TimetableResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

interface StudentService {

    @GET
    suspend fun getStart(@Url url: String): String

    @POST
    suspend fun getUserCache(
        @Url url: String,
        @Header("X-V-RequestVerificationToken") token: String,
        @Header("X-V-AppGuid") appGuid: String,
        @Header("X-V-AppVersion") appVersion: String,
        @Body body: Any = Object()
    ): ApiResponse<CacheResponse>

    @POST("UczenCache.mvc/Get")
    suspend fun getUserCache(
        @Header("X-V-RequestVerificationToken") token: String,
        @Header("X-V-AppGuid") appGuid: String,
        @Header("X-V-AppVersion") appVersion: String,
        @Body body: Any = Object()
    ): ApiResponse<CacheResponse>

    @POST
    suspend fun getSchoolInfo(@Url url: String, @Body body: Any = Object()): ApiResponse<List<Diary>>

    @POST("UczenDziennik.mvc/Get")
    suspend fun getDiaries(@Body body: Any = Object()): ApiResponse<List<Diary>>

    @POST("Oceny.mvc/Get")
    suspend fun getGrades(@Body gradeRequest: GradeRequest): ApiResponse<GradesResponse>

    @POST("Statystyki.mvc/GetOcenyCzastkowe")
    suspend fun getGradesPartialStatistics(@Body gradesStatisticsRequest: GradesStatisticsRequest): ApiResponse<List<GradesStatisticsPartial>>

    @POST("Statystyki.mvc/GetPunkty")
    suspend fun getGradesPointsStatistics(@Body gradesStatisticsRequest: GradesStatisticsRequest): ApiResponse<List<GradePointsSummary>>

    @POST("Statystyki.mvc/GetOcenyRoczne")
    suspend fun getGradesAnnualStatistics(@Body gradesStatisticsRequest: GradesStatisticsRequest): ApiResponse<List<GradesStatisticsAnnual>>

    @POST("Frekwencja.mvc/Get")
    suspend fun getAttendance(@Body attendanceRequest: AttendanceRequest): ApiResponse<AttendanceResponse>

    @POST("FrekwencjaStatystyki.mvc/Get")
    suspend fun getAttendanceStatistics(@Body attendanceSummaryRequest: AttendanceSummaryRequest): ApiResponse<AttendanceSummaryResponse>

    @POST("FrekwencjaStatystykiPrzedmioty.mvc/Get")
    suspend fun getAttendanceSubjects(@Body body: Any = Object()): ApiResponse<List<Subject>>

    @POST("Usprawiedliwienia.mvc/Post")
    suspend fun excuseForAbsence(
        @Header("X-V-RequestVerificationToken") token: String,
        @Header("X-V-AppGuid") appGuid: String,
        @Header("X-V-AppVersion") appVersion: String,
        @Body attendanceExcuseRequest: AttendanceExcuseRequest
    ): ApiResponse<Nothing>

    @POST("EgzaminyZewnetrzne.mvc/Get")
    suspend fun getExternalExaminations()

    @POST("Sprawdziany.mvc/Get")
    suspend fun getExams(@Body examRequest: ExamRequest): ApiResponse<List<ExamResponse>>

    @POST("ZadaniaDomowe.mvc/Get")
    suspend fun getZadaniaDomowe(@Body homeworkRequest: ExamRequest): ApiResponse<List<HomeworkResponse>>

    @POST("Homework.mvc/Get")
    suspend fun getHomework(@Body homeworkRequest: HomeworkRequest): ApiResponse<List<HomeworkDay>>

    @POST("PlanZajec.mvc/Get")
    suspend fun getTimetable(@Body timetableRequest: TimetableRequest): ApiResponse<TimetableResponse>

    @POST("LekcjeZrealizowane.mvc/GetPrzedmioty")
    suspend fun getRealizedSubjects(@Body body: Any = Object())

    @POST("LekcjeZrealizowane.mvc/GetZrealizowane")
    suspend fun getCompletedLessons(@Body completedLessonsRequest: CompletedLessonsRequest): String

    @POST("UwagiIOsiagniecia.mvc/Get")
    suspend fun getNotes(@Body body: Any = Object()): ApiResponse<NotesResponse>

    @POST("ZarejestrowaneUrzadzenia.mvc/Get")
    suspend fun getRegisteredDevices(@Body body: Any = Object()): ApiResponse<List<Device>>

    @POST("RejestracjaUrzadzeniaToken.mvc/Get")
    suspend fun getToken(@Body body: Any = Object()): ApiResponse<TokenResponse>

    @POST("ZarejestrowaneUrzadzenia.mvc/Delete")
    suspend fun unregisterDevice(
        @Header("X-V-RequestVerificationToken") token: String,
        @Header("X-V-AppGuid") appGuid: String,
        @Header("X-V-AppVersion") appVersion: String,
        @Body unregisterDeviceRequest: UnregisterDeviceRequest
    ): ApiResponse<Nothing>

    @POST("SzkolaINauczyciele.mvc/Get")
    suspend fun getSchoolAndTeachers(@Body body: Any = Object()): ApiResponse<SchoolAndTeachersResponse>

    @POST("Uczen.mvc/Get")
    suspend fun getStudentInfo()
}

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
import io.github.wulkanowy.sdk.scrapper.grades.GradesStatisticsRequest
import io.github.wulkanowy.sdk.scrapper.grades.GradesStatisticsResponse
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
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

interface StudentService {

    @GET
    fun getStart(@Url url: String): Single<String>

    @POST
    fun getUserCache(
        @Url url: String,
        @Header("X-V-RequestVerificationToken") token: String,
        @Header("X-V-AppGuid") appGuid: String,
        @Header("X-V-AppVersion") appVersion: String
    ): Single<ApiResponse<CacheResponse>>

    @POST("UczenCache.mvc/Get")
    fun getUserCache(
        @Header("X-V-RequestVerificationToken") token: String,
        @Header("X-V-AppGuid") appGuid: String,
        @Header("X-V-AppVersion") appVersion: String
    ): Single<ApiResponse<CacheResponse>>

    @POST
    fun getSchoolInfo(@Url url: String): Single<ApiResponse<List<Diary>>>

    @POST("UczenDziennik.mvc/Get")
    fun getDiaries(): Single<ApiResponse<List<Diary>>>

    @POST("Oceny.mvc/Get")
    fun getGrades(@Body gradeRequest: GradeRequest): Single<ApiResponse<GradesResponse>>

    @POST("Statystyki.mvc/GetOcenyCzastkowe")
    fun getGradesPartialStatistics(@Body gradesStatisticsRequest: GradesStatisticsRequest): Single<ApiResponse<List<GradesStatisticsResponse.Partial>>>

    @POST("Statystyki.mvc/GetPunkty")
    fun getGradesPointsStatistics(@Body gradesStatisticsRequest: GradesStatisticsRequest): Single<ApiResponse<List<GradePointsSummary>>>

    @POST("Statystyki.mvc/GetOcenyRoczne")
    fun getGradesAnnualStatistics(@Body gradesStatisticsRequest: GradesStatisticsRequest): Single<ApiResponse<List<GradesStatisticsResponse.Annual>>>

    @POST("Frekwencja.mvc/Get")
    fun getAttendance(@Body attendanceRequest: AttendanceRequest): Single<ApiResponse<AttendanceResponse>>

    @POST("FrekwencjaStatystyki.mvc/Get")
    fun getAttendanceStatistics(@Body attendanceSummaryRequest: AttendanceSummaryRequest): Single<ApiResponse<AttendanceSummaryResponse>>

    @POST("FrekwencjaStatystykiPrzedmioty.mvc/Get")
    fun getAttendanceSubjects(): Single<ApiResponse<List<Subject>>>

    @POST("Usprawiedliwienia.mvc/Post")
    fun excuseForAbsence(
        @Header("X-V-RequestVerificationToken") token: String,
        @Header("X-V-AppGuid") appGuid: String,
        @Header("X-V-AppVersion") appVersion: String,
        @Body attendanceExcuseRequest: AttendanceExcuseRequest
    ): Single<ApiResponse<Nothing>>

    @POST("EgzaminyZewnetrzne.mvc/Get")
    fun getExternalExaminations()

    @POST("Sprawdziany.mvc/Get")
    fun getExams(@Body examRequest: ExamRequest): Single<ApiResponse<List<ExamResponse>>>

    @POST("Homework.mvc/Get")
    fun getHomework(@Body homeworkRequest: ExamRequest): Single<ApiResponse<List<HomeworkResponse>>>

    @POST("PlanZajec.mvc/Get")
    fun getTimetable(@Body timetableRequest: TimetableRequest): Single<ApiResponse<TimetableResponse>>

    @POST("LekcjeZrealizowane.mvc/GetPrzedmioty")
    fun getRealizedSubjects()

    @POST("LekcjeZrealizowane.mvc/GetZrealizowane")
    fun getCompletedLessons(@Body completedLessonsRequest: CompletedLessonsRequest): Single<String>

    @POST("UwagiIOsiagniecia.mvc/Get")
    fun getNotes(): Single<ApiResponse<NotesResponse>>

    @POST("ZarejestrowaneUrzadzenia.mvc/Get")
    fun getRegisteredDevices(): Single<ApiResponse<List<Device>>>

    @POST("RejestracjaUrzadzeniaToken.mvc/Get")
    fun getToken(): Single<ApiResponse<TokenResponse>>

    @POST("ZarejestrowaneUrzadzenia.mvc/Delete")
    fun unregisterDevice(
        @Header("X-V-RequestVerificationToken") token: String,
        @Header("X-V-AppGuid") appGuid: String,
        @Header("X-V-AppVersion") appVersion: String,
        @Body unregisterDeviceRequest: UnregisterDeviceRequest
    ): Single<ApiResponse<Nothing>>

    @POST("SzkolaINauczyciele.mvc/Get")
    fun getSchoolAndTeachers(): Single<ApiResponse<SchoolAndTeachersResponse>>

    @POST("Uczen.mvc/Get")
    fun getStudentInfo()
}

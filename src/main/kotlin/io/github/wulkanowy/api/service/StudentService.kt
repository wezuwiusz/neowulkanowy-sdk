package io.github.wulkanowy.api.service

import io.github.wulkanowy.api.ApiResponse
import io.github.wulkanowy.api.attendance.AttendanceExcuseRequest
import io.github.wulkanowy.api.attendance.AttendanceRequest
import io.github.wulkanowy.api.attendance.AttendanceResponse
import io.github.wulkanowy.api.attendance.AttendanceSummaryRequest
import io.github.wulkanowy.api.attendance.AttendanceSummaryResponse
import io.github.wulkanowy.api.attendance.Subject
import io.github.wulkanowy.api.exams.ExamRequest
import io.github.wulkanowy.api.exams.ExamResponse
import io.github.wulkanowy.api.grades.GradePointsSummary
import io.github.wulkanowy.api.grades.GradeRequest
import io.github.wulkanowy.api.grades.GradesResponse
import io.github.wulkanowy.api.grades.GradesStatisticsRequest
import io.github.wulkanowy.api.grades.GradesStatisticsResponse
import io.github.wulkanowy.api.homework.HomeworkResponse
import io.github.wulkanowy.api.mobile.Device
import io.github.wulkanowy.api.mobile.TokenResponse
import io.github.wulkanowy.api.mobile.UnregisterDeviceRequest
import io.github.wulkanowy.api.notes.NotesResponse
import io.github.wulkanowy.api.register.Diary
import io.github.wulkanowy.api.school.SchoolAndTeachersResponse
import io.github.wulkanowy.api.timetable.CacheResponse
import io.github.wulkanowy.api.timetable.CompletedLessonsRequest
import io.github.wulkanowy.api.timetable.TimetableRequest
import io.github.wulkanowy.api.timetable.TimetableResponse
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

interface StudentService {

    @GET
    fun getStart(@Url url: String): Single<String>

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
    fun excuseForAbsence(@Body attendanceExcuseRequest: AttendanceExcuseRequest): Single<ApiResponse<Nothing>>

    @POST("EgzaminyZewnetrzne.mvc/Get")
    fun getExternalExaminations()

    @POST("Sprawdziany.mvc/Get")
    fun getExams(@Body examRequest: ExamRequest): Single<ApiResponse<List<ExamResponse>>>

    @POST("ZadaniaDomowe.mvc/Get")
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

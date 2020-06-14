package io.github.wulkanowy.sdk.scrapper.service

import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceResponse
import io.github.wulkanowy.sdk.scrapper.exams.ExamResponse
import io.github.wulkanowy.sdk.scrapper.grades.GradesResponse
import io.github.wulkanowy.sdk.scrapper.grades.GradesStatisticsResponse
import io.github.wulkanowy.sdk.scrapper.grades.GradesSummaryResponse
import io.github.wulkanowy.sdk.scrapper.homework.HomeworkResponse
import io.github.wulkanowy.sdk.scrapper.mobile.RegisteredDevicesResponse
import io.github.wulkanowy.sdk.scrapper.mobile.TokenResponse
import io.github.wulkanowy.sdk.scrapper.notes.NotesResponse
import io.github.wulkanowy.sdk.scrapper.register.StudentAndParentResponse
import io.github.wulkanowy.sdk.scrapper.school.SchoolAndTeachersResponse
import io.github.wulkanowy.sdk.scrapper.student.StudentInfo
import io.github.wulkanowy.sdk.scrapper.timetable.RealizedResponse
import io.github.wulkanowy.sdk.scrapper.timetable.TimetableResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface StudentAndParentService {

    @GET
    suspend fun getSchoolInfo(@Url url: String): StudentAndParentResponse

    @GET("Uczen/UczenOnChange")
    suspend fun getUserInfo(@Query("id") userId: Int): StudentAndParentResponse

    @GET("Dziennik/DziennikOnChange")
    suspend fun getDiaryInfo(@Query("id") diaryId: Int, @Header("Referer") referer: String): StudentAndParentResponse

    @GET("Frekwencja.mvc")
    suspend fun getAttendance(@Query("data") date: String): AttendanceResponse

    @GET("Frekwencja.mvc")
    suspend fun getAttendanceSummary(@Query("idPrzedmiot") subjectId: Int?): AttendanceResponse

    @GET("Sprawdziany.mvc/Terminarz?rodzajWidoku=2")
    suspend fun getExams(@Query("data") date: String): ExamResponse

    @GET("Oceny/Wszystkie?details=2")
    suspend fun getGrades(@Query("okres") semester: Int?): GradesResponse

    @GET("Oceny/Wszystkie?details=1")
    suspend fun getGradesSummary(@Query("okres") semester: Int?): GradesSummaryResponse

    @GET("Statystyki.mvc/Uczen")
    suspend fun getGradesStatistics(@Query("rodzajWidoku") type: Int?, @Query("semestr") semesterId: Int): GradesStatisticsResponse

    @GET("ZadaniaDomowe.mvc?rodzajWidoku=Dzien")
    suspend fun getHomework(@Query("data") date: String): HomeworkResponse

    @GET("UwagiOsiagniecia.mvc/Wszystkie")
    suspend fun getNotes(): NotesResponse

    @GET("DostepMobilny.mvc")
    suspend fun getRegisteredDevices(): RegisteredDevicesResponse

    @GET("DostepMobilny.mvc/Rejestruj")
    suspend fun getToken(): TokenResponse

    @POST("DostepMobilny.mvc/PotwierdzWyrejestrowanie")
    @FormUrlEncoded
    suspend fun unregisterDevice(@Field("Id") id: Int): RegisteredDevicesResponse

    @GET("Szkola.mvc/Nauczyciele")
    suspend fun getSchoolAndTeachers(): SchoolAndTeachersResponse

    @GET("Lekcja.mvc/PlanZajec")
    suspend fun getTimetable(@Query("data") date: String): TimetableResponse

    @GET("Lekcja.mvc/Zrealizowane")
    suspend fun getCompletedLessons(@Query("start") start: String, @Query("end") end: String?, @Query("idPrzedmiot") subjectId: Int?): RealizedResponse

    @GET("Uczen.mvc/DanePodstawowe")
    suspend fun getStudentInfo(): StudentInfo
}

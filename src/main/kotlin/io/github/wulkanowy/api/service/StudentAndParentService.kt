package io.github.wulkanowy.api.service

import io.github.wulkanowy.api.attendance.AttendanceResponse
import io.github.wulkanowy.api.exams.ExamResponse
import io.github.wulkanowy.api.grades.GradesResponse
import io.github.wulkanowy.api.grades.GradesSummaryResponse
import io.github.wulkanowy.api.homework.HomeworkResponse
import io.github.wulkanowy.api.mobile.RegisteredDevicesResponse
import io.github.wulkanowy.api.mobile.TokenResponse
import io.github.wulkanowy.api.notes.NotesResponse
import io.github.wulkanowy.api.register.StudentAndParentResponse
import io.github.wulkanowy.api.school.SchoolAndTeachersResponse
import io.github.wulkanowy.api.student.StudentInfo
import io.github.wulkanowy.api.timetable.TimetableResponse
import io.reactivex.Single
import retrofit2.http.*

interface StudentAndParentService {

    @GET("Start/Index/")
    fun getSchoolInfo(): Single<StudentAndParentResponse>

    @GET
    fun getSchoolInfo(@Url url: String): Single<StudentAndParentResponse>

    @GET("Uczen/UczenOnChange")
    fun getUserInfo(@Query("id") userId: String): Single<StudentAndParentResponse>

    @GET("Dziennik/DziennikOnChange")
    fun getDiaryInfo(@Query("id") diaryId: String, @Header("Referer") referer: String): Single<StudentAndParentResponse>

    @GET("Frekwencja.mvc")
    fun getAttendance(@Query("data") date: String): Single<AttendanceResponse>

    @GET("Sprawdziany.mvc/Terminarz?rodzajWidoku=2")
    fun getExams(@Query("data") date: String): Single<ExamResponse>

    @GET("Oceny/Wszystkie?details=2")
    fun getGrades(@Query("okres") semester: Int?): Single<GradesResponse>

    @GET("Oceny/Wszystkie?details=1")
    fun getGradesSummary(@Query("okres") semester: Int?): Single<GradesSummaryResponse>

    @GET("ZadaniaDomowe.mvc?rodzajWidoku=Dzien")
    fun getHomework(@Query("data") date: String): Single<HomeworkResponse>

    @GET("UwagiOsiagniecia.mvc/Wszystkie")
    fun getNotes(): Single<NotesResponse>

    @GET("DostepMobilny.mvc")
    fun getRegisteredDevices(): Single<RegisteredDevicesResponse>

    @GET("DostepMobilny.mvc/Rejestruj")
    fun getToken(): Single<TokenResponse>

    @POST("DostepMobilny.mvc/PotwierdzWyrejestrowanie")
    @FormUrlEncoded
    fun unregisterDevice(@Field("Id") id: Int): Single<RegisteredDevicesResponse>

    @GET("Szkola.mvc/Nauczyciele")
    fun getSchoolAndTeachers(): Single<SchoolAndTeachersResponse>

    @GET("Lekcja.mvc/PlanZajec")
    fun getTimetable(@Query("data") date: String): Single<TimetableResponse>

    @GET("Uczen.mvc/DanePodstawowe")
    fun getStudentInfo(): Single<StudentInfo>
}

package io.github.wulkanowy.api.service

import io.github.wulkanowy.api.ApiResponse
import io.github.wulkanowy.api.grades.GradeRequest
import io.github.wulkanowy.api.grades.GradesResponse
import io.github.wulkanowy.api.mobile.Device
import io.github.wulkanowy.api.register.Diary
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface StudentService {

    @POST("UczenCache.mvc/Get")
    fun getUserCache()

    @POST("UczenDziennik.mvc/Get")
    fun getDiaries(): Single<ApiResponse<List<Diary>>>

    @POST("Oceny.mvc/Get")
    fun getGrades(@Body gradeRequest: GradeRequest): Single<ApiResponse<GradesResponse>>

    @POST("Frekwencja.mvc/Get")
    fun getAttendance()

    @POST("FrekwencjaStatystyki.mvc/Get")
    fun getAttendanceStatistics()

    @POST("FrekwencjaStatystykiPrzedmioty.mvc/Get")
    fun getAttendanceSubjects()

    @POST("EgzaminyZewnetrzne.mvc/Get")
    fun getExternalExaminations()

    @POST("Sprawdziany.mvc/Get")
    fun getExams()

    @POST("ZadaniaDomowe.mvc/Get")
    fun getHomework()

    @POST("PlanZajec.mvc/Get")
    fun getTimetable()

    @POST("LekcjeZrealizowane.mvc/GetPrzedmioty")
    fun getRealizedSubjects()

    @POST("LekcjeZrealizowane.mvc/GetZrealizowane")
    fun getRealizedLessons()

    @POST("UwagiIOsiagniecia.mvc/Get")
    fun getNotes()

    @POST("ZarejestrowaneUrzadzenia.mvc/Get")
    fun getRegisteredDevices(): Single<ApiResponse<List<Device>>>

    @POST("RejestracjaUrzadzeniaToken.mvc/Get")
    fun getToken()

    @POST("ZarejestrowaneUrzadzenia.mvc/Delete")
    fun unregisterDevice()

    @POST("SzkolaINauczyciele.mvc/Get")
    fun getSchoolAndTeachers()

    @POST("Uczen.mvc/Get")
    fun getStudentInfo()
}

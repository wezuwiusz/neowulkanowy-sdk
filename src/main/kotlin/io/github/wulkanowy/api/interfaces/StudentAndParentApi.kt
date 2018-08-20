package io.github.wulkanowy.api.interfaces

import io.github.wulkanowy.api.grades.GradesResponse
import io.github.wulkanowy.api.notes.NotesResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface StudentAndParentApi {

    @GET("Frekwencja.mvc")
    fun getAttendance(@Query("data") date: String)

    @GET("Sprawdziany.mvc/Terminarz?rodzajWidoku=2")
    fun getExams(@Query("date") date: String)

    @GET("Oceny/Wszystkie?details=2")
    fun getGrades(@Query("okres") semester: Int): Observable<GradesResponse>

    @GET("ZadaniaDomowe.mvc?rodzajWidoku=Dzien")
    fun getHomework(@Query("data") date: String)

    @GET("DostepMobilny.mvc")
    fun getRegisteredDevices()

    @GET("UwagiOsiagniecia.mvc/Wszystkie")
    fun getNotes(): Observable<NotesResponse>

    @GET("Szkola.mvc/Nauczyciele")
    fun getTeachers()

    @GET("Lekcja.mvc/PlanZajec")
    fun getTimetable(@Query("data") date: String)

    @GET("Uczen.mvc/DanePodstawowe")
    fun getUserInfo()
}

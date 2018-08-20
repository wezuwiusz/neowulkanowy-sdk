package io.github.wulkanowy.api.repository

import io.github.wulkanowy.api.interfaces.StudentAndParentApi
import io.github.wulkanowy.api.notes.Note
import io.reactivex.Observable
import okhttp3.OkHttpClient
import pl.droidsonroids.retrofit2.JspoonConverterFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

class StudentAndParentRepository(private val host: String,
                                 private val symbol: String,
                                 private val schoolId: String,
                                 private val client: OkHttpClient
) {

    private val api by lazy { getMobileApi() }

    fun getTimetable(startDate: String) = api.getTimetable(startDate)

    fun getGrades(classificationPeriodId: Int) = api.getGrades(classificationPeriodId)

    fun getExams(startDate: String) = api.getExams(startDate)

    fun getNotes(): Observable<List<Note>> {
        return api.getNotes().map {
            it.notes.mapIndexed { i, note ->
                note.date = it.dates[i]
                note
            }
        }
    }

    fun getAttendance(startDate: String) = api.getAttendance(startDate)

    fun getHomework(startDate: String) = api.getHomework(startDate)

    private fun getMobileApi(): StudentAndParentApi {
        return Retrofit.Builder()
                .baseUrl("$host/$symbol/$schoolId/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JspoonConverterFactory.create())
                .client(client)
                .build()
                .create(StudentAndParentApi::class.java)
    }
}

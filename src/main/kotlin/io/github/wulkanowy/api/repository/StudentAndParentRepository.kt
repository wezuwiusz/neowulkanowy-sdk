package io.github.wulkanowy.api.repository

import io.github.wulkanowy.api.attendance.Attendance
import io.github.wulkanowy.api.exams.Exam
import io.github.wulkanowy.api.grades.Grade
import io.github.wulkanowy.api.interfaces.StudentAndParentApi
import io.github.wulkanowy.api.notes.Note
import io.reactivex.Single
import okhttp3.OkHttpClient
import pl.droidsonroids.retrofit2.JspoonConverterFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

class StudentAndParentRepository(
        private val ssl: Boolean,
        private val host: String,
        private val symbol: String,
        private val schoolId: String,
        private val client: OkHttpClient
) {

    private val api by lazy { getMobileApi() }

    fun getTimetable(startDate: String) = api.getTimetable(startDate)

    fun getGrades(classificationPeriodId: Int): Single<List<Grade>> {
        return api.getGrades(classificationPeriodId).map {
            it.grades.mapNotNull { grade ->
                if (grade.value == "Brak ocen") return@mapNotNull null
                if (grade.description == grade.symbol) grade.description = ""
                grade
            }
        }
    }

    fun getExams(startDate: String): Single<List<Exam>> {
        return api.getExams(startDate).map { res ->
            res.days.flatMap { day ->
                day.exams.map { exam ->
                    exam.date = day.date
                    exam
                }
            }
        }
    }

    fun getNotes(): Single<List<Note>> {
        return api.getNotes().map {
            it.notes.mapIndexed { i, note ->
                note.date = it.dates[i]
                note
            }
        }
    }

    fun getAttendance(startDate: String): Single<List<Attendance>> {
        return api.getAttendance(startDate).map { res ->
            res.rows.flatMap { row ->
                row.lessons.mapIndexedNotNull { i, it ->
                    if ("null" == it.subject) return@mapIndexedNotNull null
                    it.date = res.days[i]
                    it.number = row.number
                    it
                }
            }.sortedBy { it.date }
        }
    }

    fun getHomework(startDate: String) = api.getHomework(startDate)

    private fun getMobileApi(): StudentAndParentApi {
        val schema = "http" + if (ssl) "s" else ""
        return Retrofit.Builder()
                .baseUrl("$schema://uonetplus-opiekun.$host/$symbol/$schoolId/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JspoonConverterFactory.create())
                .client(client)
                .build()
                .create(StudentAndParentApi::class.java)
    }
}

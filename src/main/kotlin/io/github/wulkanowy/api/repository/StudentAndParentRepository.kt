package io.github.wulkanowy.api.repository

import io.github.wulkanowy.api.attendance.Attendance
import io.github.wulkanowy.api.exams.Exam
import io.github.wulkanowy.api.grades.Grade
import io.github.wulkanowy.api.grades.Summary
import io.github.wulkanowy.api.homework.Homework
import io.github.wulkanowy.api.interfaces.StudentAndParentApi
import io.github.wulkanowy.api.notes.Note
import io.github.wulkanowy.api.student.StudentInfo
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

    private val api by lazy {
        val schema = "http" + if (ssl) "s" else ""
        Retrofit.Builder()
                .baseUrl("$schema://uonetplus-opiekun.$host/$symbol/$schoolId/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JspoonConverterFactory.create())
                .client(client)
                .build()
                .create(StudentAndParentApi::class.java)
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

    fun getGrades(classificationPeriodId: Int): Single<List<Grade>> {
        return api.getGrades(classificationPeriodId).map {
            it.grades.mapNotNull { grade ->
                if (grade.value == "Brak ocen") return@mapNotNull null
                if (grade.description == grade.symbol) grade.description = ""
                grade
            }
        }
    }

    fun getGradesSummary(classificationPeriodId: Int): Single<List<Summary>> {
        return api.getGradesSummary(classificationPeriodId).map {
            it.subjects.map { summary ->
                summary.predicted = getGradeShortValue(summary.predicted)
                summary.final = getGradeShortValue(summary.final)
                summary
            }
        }
    }

    fun getHomework(date: String): Single<List<Homework>> {
        return api.getHomework(date).map {
            it.items.map { item ->
                item.date = it.date
                item
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

    fun getStudentInfo(): Single<StudentInfo> {
        return api.getStudentInfo().map {
            it.student.polishCitizenship = if ("Tak" == it.student.polishCitizenship) "1" else "0"
            it
        }
    }

    private fun getGradeShortValue(value: String): String {
        return when (value) {
            "celujący" -> "6"
            "bardzo dobry" -> "5"
            "dobry" -> "4"
            "dostateczny" -> "3"
            "dopuszczający" -> "2"
            "niedostateczny" -> "1"
            else -> value
        }
    }
}

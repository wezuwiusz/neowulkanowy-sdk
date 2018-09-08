package io.github.wulkanowy.api.repository

import io.github.wulkanowy.api.attendance.Attendance
import io.github.wulkanowy.api.exams.Exam
import io.github.wulkanowy.api.grades.Grade
import io.github.wulkanowy.api.grades.Summary
import io.github.wulkanowy.api.homework.Homework
import io.github.wulkanowy.api.notes.Note
import io.github.wulkanowy.api.register.StudentAndParentResponse
import io.github.wulkanowy.api.school.Teacher
import io.github.wulkanowy.api.service.StudentAndParentService
import io.github.wulkanowy.api.student.StudentInfo
import io.reactivex.Single
import java.util.*
import java.util.Calendar

class StudentAndParentRepository(private val api: StudentAndParentService) {

    fun getSchoolInfo(): Single<StudentAndParentResponse> {
        return api.getSchoolInfo()
    }

    fun getAttendance(startDate: Date?): Single<List<Attendance>> {
        return api.getAttendance(getTickFromDate(startDate)).map { res ->
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

    fun getExams(startDate: Date?): Single<List<Exam>> {
        return api.getExams(getTickFromDate(startDate)).map { res ->
            res.days.flatMap { day ->
                day.exams.map { exam ->
                    exam.date = day.date
                    if (exam.group.contains(" ")) exam.group = ""
                    exam
                }
            }
        }
    }

    fun getGrades(semesterId: Int?): Single<List<Grade>> {
        return api.getGrades(semesterId).map {
            it.grades.map { grade ->
                if (grade.description == grade.symbol) grade.description = ""
                grade
            }
        }
    }

    fun getGradesSummary(semesterId: Int?): Single<List<Summary>> {
        return api.getGradesSummary(semesterId).map {
            it.subjects.map { summary ->
                summary.predicted = getGradeShortValue(summary.predicted)
                summary.final = getGradeShortValue(summary.final)
                summary
            }
        }
    }

    fun getHomework(date: Date?): Single<List<Homework>> {
        return api.getHomework(getTickFromDate(date)).map {
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

    fun getTeachers(): Single<List<Teacher>> {
        return api.getSchoolAndTeachers().map {
            it.subjects.flatMap { subject ->
                subject.teachers.split(", ").map { teacher ->
                    val tas = teacher.split(" [")
                    Teacher(tas.first(), tas.last().removeSuffix("]"), subject.name)
                }
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

    private fun getTickFromDate(date: Date?): String {
        if (date == null) return ""
        val c = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeZone = TimeZone.getDefault()
            time = date
        }
        val utcOffset = c.get(Calendar.ZONE_OFFSET) + c.get(Calendar.DST_OFFSET)
        return ((c.timeInMillis + utcOffset) * 10000 + 621355968000000000L).toString()
    }
}

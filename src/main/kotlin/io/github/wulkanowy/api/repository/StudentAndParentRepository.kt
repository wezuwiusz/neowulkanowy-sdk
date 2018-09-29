package io.github.wulkanowy.api.repository

import io.github.wulkanowy.api.attendance.Attendance
import io.github.wulkanowy.api.attendance.AttendanceSummary
import io.github.wulkanowy.api.exams.Exam
import io.github.wulkanowy.api.grades.Grade
import io.github.wulkanowy.api.grades.GradeStatistics
import io.github.wulkanowy.api.grades.GradeSummary
import io.github.wulkanowy.api.homework.Homework
import io.github.wulkanowy.api.mobile.Device
import io.github.wulkanowy.api.mobile.TokenResponse
import io.github.wulkanowy.api.notes.Note
import io.github.wulkanowy.api.realized.Realized
import io.github.wulkanowy.api.register.StudentAndParentResponse
import io.github.wulkanowy.api.school.Teacher
import io.github.wulkanowy.api.service.StudentAndParentService
import io.github.wulkanowy.api.student.StudentInfo
import io.github.wulkanowy.api.timetable.Timetable
import io.github.wulkanowy.api.timetable.TimetableParser
import io.reactivex.Single
import java.text.SimpleDateFormat
import java.util.*

class StudentAndParentRepository(private val api: StudentAndParentService) {

    fun getSchoolInfo(): Single<StudentAndParentResponse> {
        return api.getSchoolInfo()
    }

    fun getAttendance(startDate: Date?): Single<List<Attendance>> {
        return api.getAttendance(startDate.toTick()).map { res ->
            res.rows.flatMap { row ->
                row.lessons.mapIndexed { i, it ->
                    it.date = res.days[i]
                    it.number = row.number
                    it.presence = it.name == Attendance.Types.PRESENCE || it.name == Attendance.Types.ABSENCE_FOR_SCHOOL_REASONS
                    it.absence = it.name == Attendance.Types.ABSENCE_UNEXCUSED || it.name == Attendance.Types.ABSENCE_EXCUSED
                    it.lateness = it.name == Attendance.Types.EXCUSED_LATENESS || it.name == Attendance.Types.UNEXCUSED_LATENESS
                    it.excused = it.name == Attendance.Types.ABSENCE_EXCUSED || it.name == Attendance.Types.EXCUSED_LATENESS
                    it.exemption = it.name == Attendance.Types.EXEMPTION
                    it.name = when (it.name) {
                        Attendance.Types.PRESENCE -> "Obecność"
                        Attendance.Types.ABSENCE_UNEXCUSED -> "Nieobecność nieusprawiedliwiona"
                        Attendance.Types.ABSENCE_EXCUSED -> "Nieobecność usprawiedliwiona"
                        Attendance.Types.ABSENCE_FOR_SCHOOL_REASONS -> "Nieobecność z przyczyn szkolnych"
                        Attendance.Types.EXEMPTION -> "Zwolnienie"
                        Attendance.Types.UNEXCUSED_LATENESS -> "Spóźnienie nieusprawiedliwione"
                        Attendance.Types.EXCUSED_LATENESS -> "Spóźnienie usprawiedliwione"
                        else -> ""
                    }
                    it
                }
            }.sortedWith(compareBy({ it.date }, { it.number }))
        }
    }

    fun getAttendanceSummary(subjectId: Int?): Single<List<AttendanceSummary>> {
        return api.getAttendanceSummary(subjectId).map { res ->
            res.days.mapIndexed { i, day ->
                AttendanceSummary(day,
                        res.rows[0].value[i].toIntOrNull() ?: 0,
                        res.rows[1].value[i].toIntOrNull() ?: 0,
                        res.rows[2].value[i].toIntOrNull() ?: 0,
                        res.rows[3].value[i].toIntOrNull() ?: 0,
                        res.rows[4].value[i].toIntOrNull() ?: 0,
                        res.rows[5].value[i].toIntOrNull() ?: 0,
                        res.rows[6].value[i].toIntOrNull() ?: 0
                )
            }
        }
    }

    fun getExams(startDate: Date?): Single<List<Exam>> {
        return api.getExams(startDate.toTick()).map { res ->
            res.days.flatMap { day ->
                day.exams.map { exam ->
                    exam.date = day.date
                    if (exam.group.contains(" ")) exam.group = ""
                    exam
                }
            }.sortedBy { it.date }
        }
    }

    fun getGrades(semesterId: Int?): Single<List<Grade>> {
        return api.getGrades(semesterId).map { res ->
            res.grades.asSequence().map { grade ->
                if (grade.entry == grade.comment) grade.comment = ""
                if (grade.description == grade.symbol) grade.description = ""
                grade
            }.sortedWith(compareBy({ it.date }, { it.subject })).toList()
        }
    }

    fun getGradesSummary(semesterId: Int?): Single<List<GradeSummary>> {
        return api.getGradesSummary(semesterId).map { res ->
            res.subjects.asSequence().map { summary ->
                summary.predicted = getGradeShortValue(summary.predicted)
                summary.final = getGradeShortValue(summary.final)
                summary
            }.sortedBy { it.name }.toList()
        }
    }

    fun getGradesStatistics(semesterId: Int?, annual: Boolean): Single<List<GradeStatistics>> {
        return api.getGradesStatistics(if (!annual) 1 else 2, semesterId).map { res ->
            res.items.map {
                it.apply {
                    this.gradeValue = getGradeShortValue(this.grade).toIntOrNull() ?: 0
                    this.semesterId = res.semesterId
                }
            }
        }
    }

    fun getHomework(date: Date?): Single<List<Homework>> {
        return api.getHomework(date.toTick()).map { res ->
            res.items.asSequence().map { item ->
                item.date = res.date
                item
            }.sortedWith(compareBy({ it.date }, { it.subject })).toList()
        }
    }

    fun getNotes(): Single<List<Note>> {
        return api.getNotes().map { res ->
            res.notes.asSequence().mapIndexed { i, note ->
                note.date = res.dates[i]
                note
            }.sortedWith(compareBy({ it.date }, { it.category })).toList()
        }
    }

    fun getRegisteredDevices(): Single<List<Device>> {
        return api.getRegisteredDevices().map { it.devices }
    }

    fun getToken(): Single<TokenResponse> {
        return api.getToken()
    }

    fun unregisterDevice(id: Int): Single<List<Device>> {
        return api.unregisterDevice(id).map { it.devices }
    }

    fun getTeachers(): Single<List<Teacher>> {
        return api.getSchoolAndTeachers().map { res ->
            res.subjects.flatMap { subject ->
                subject.teachers.split(", ").map { teacher ->
                    val tas = teacher.split(" [")
                    Teacher(tas.first(), tas.last().removeSuffix("]"), subject.name)
                }
            }.sortedWith(compareBy({ it.subject }, { it.name }))
        }
    }

    fun getStudentInfo(): Single<StudentInfo> {
        return api.getStudentInfo().map {
            it.student.polishCitizenship = if ("Tak" == it.student.polishCitizenship) "1" else "0"
            it
        }
    }

    fun getTimetable(startDate: Date?): Single<List<Timetable>> {
        return api.getTimetable(startDate.toTick()).map { res ->
            res.rows.flatMap { row ->
                row.lessons.asSequence().mapIndexedNotNull { i, it ->
                    it.date = res.days[i]
                    it.start = "${it.date.toString("yyy-MM-dd")} ${row.startTime}".toDate("yyyy-MM-dd HH:mm")
                    it.end = "${it.date.toString("yyy-MM-dd")} ${row.endTime}".toDate("yyyy-MM-dd HH:mm")
                    it.number = row.number
                    it
                }.mapNotNull { TimetableParser().getTimetable(it) }.toList()
            }.sortedWith(compareBy({ it.date }, { it.number }))
        }
    }

    fun getRealized(startDate: Date?): Single<List<Realized>> {
        return api.getRealized(startDate.toTick(), null, null).map { res ->
            lateinit var lastDate: Date
            res.items.asSequence().mapNotNull {
                if (it.subject.isBlank()) {
                    lastDate = it.date
                    return@mapNotNull null
                }

                it.apply { date = lastDate }
            }.sortedWith(compareBy({ it.date }, { it.number })).toList()
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

    private fun String.toDate(format: String): Date = SimpleDateFormat(format).parse(this)

    private fun Date.toString(format: String): String = SimpleDateFormat(format).format(this)

    private fun Date?.toTick(): String {
        if (this == null) return ""
        val c = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeZone = TimeZone.getDefault()
            time = this@toTick
        }
        val utcOffset = c.get(Calendar.ZONE_OFFSET) + c.get(Calendar.DST_OFFSET)
        return ((c.timeInMillis + utcOffset) * 10000 + 621355968000000000L).toString()
    }
}

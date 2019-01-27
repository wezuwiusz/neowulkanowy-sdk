package io.github.wulkanowy.api.repository

import io.github.wulkanowy.api.attendance.Attendance
import io.github.wulkanowy.api.attendance.AttendanceSummary
import io.github.wulkanowy.api.attendance.Subject
import io.github.wulkanowy.api.exams.Exam
import io.github.wulkanowy.api.getGradeShortValue
import io.github.wulkanowy.api.getLastMonday
import io.github.wulkanowy.api.grades.Grade
import io.github.wulkanowy.api.grades.GradeStatistics
import io.github.wulkanowy.api.grades.GradeSummary
import io.github.wulkanowy.api.homework.Homework
import io.github.wulkanowy.api.mobile.Device
import io.github.wulkanowy.api.mobile.TokenResponse
import io.github.wulkanowy.api.notes.Note
import io.github.wulkanowy.api.realized.Realized
import io.github.wulkanowy.api.school.School
import io.github.wulkanowy.api.school.Teacher
import io.github.wulkanowy.api.service.StudentAndParentService
import io.github.wulkanowy.api.student.StudentInfo
import io.github.wulkanowy.api.timetable.Timetable
import io.github.wulkanowy.api.timetable.TimetableParser
import io.github.wulkanowy.api.toDate
import io.github.wulkanowy.api.toFormat
import io.github.wulkanowy.api.toLocalDate
import io.reactivex.Single
import org.threeten.bp.LocalDate
import org.threeten.bp.Month
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

class StudentAndParentRepository(private val api: StudentAndParentService) {

    fun getAttendance(startDate: LocalDate, endDate: LocalDate? = null): Single<List<Attendance>> {
        val end = endDate ?: startDate.plusDays(4)
        return api.getAttendance(startDate.getLastMonday().toTick()).map { res ->
            res.rows.flatMap { row ->
                row.lessons.mapIndexedNotNull { i, it ->
                    if ("null" == it.subject) return@mapIndexedNotNull null // fix empty months
                    it.apply {
                        date = res.days[i]
                        number = row.number
                        presence = it.type == Attendance.Types.PRESENCE || it.type == Attendance.Types.ABSENCE_FOR_SCHOOL_REASONS
                        absence = it.type == Attendance.Types.ABSENCE_UNEXCUSED || it.type == Attendance.Types.ABSENCE_EXCUSED
                        lateness = it.type == Attendance.Types.EXCUSED_LATENESS || it.type == Attendance.Types.UNEXCUSED_LATENESS
                        excused = it.type == Attendance.Types.ABSENCE_EXCUSED || it.type == Attendance.Types.EXCUSED_LATENESS
                        exemption = it.type == Attendance.Types.EXEMPTION
                    }
                }
            }.asSequence().filter {
                it.date.toLocalDate() >= startDate && it.date.toLocalDate() <= end
            }.sortedWith(compareBy({ it.date }, { it.number })).toList()
        }
    }

    fun getAttendanceSummary(subjectId: Int?): Single<List<AttendanceSummary>> {
        return api.getAttendanceSummary(subjectId).map { res ->
            res.months.mapIndexedNotNull { i, month ->
                if (res.summaryRows.all { it.value[i].isBlank() }) return@mapIndexedNotNull null
                AttendanceSummary(romanToMonthEnum(month),
                        res.summaryRows[0].value[i].toIntOrNull() ?: 0,
                        res.summaryRows[1].value[i].toIntOrNull() ?: 0,
                        res.summaryRows[2].value[i].toIntOrNull() ?: 0,
                        res.summaryRows[3].value[i].toIntOrNull() ?: 0,
                        res.summaryRows[4].value[i].toIntOrNull() ?: 0,
                        res.summaryRows[5].value[i].toIntOrNull() ?: 0,
                        res.summaryRows[6].value[i].toIntOrNull() ?: 0
                )
            }
        }
    }

    fun getSubjects(): Single<List<Subject>> {
        return api.getAttendanceSummary(-1).map { it.subjects }
    }

    fun getExams(startDate: LocalDate, endDate: LocalDate? = null): Single<List<Exam>> {
        val end = endDate ?: startDate.plusDays(4)
        return api.getExams(startDate.getLastMonday().toTick()).map { res ->
            res.days.flatMap { day ->
                day.exams.map { exam ->
                    exam.apply {
                        if (group.contains(" ")) group = ""
                        date = day.date
                    }
                }
            }.asSequence().filter {
                it.date.toLocalDate() >= startDate && it.date.toLocalDate() <= end
            }.sortedBy { it.date }.toList()
        }
    }

    fun getGrades(semesterId: Int?): Single<List<Grade>> {
        return api.getGrades(semesterId).map { res ->
            res.grades.asSequence().map { grade ->
                grade.apply {
                    if (entry == comment) comment = ""
                    if (description == symbol) description = ""
                }
            }.toList().sortedByDescending { it.date }
        }
    }

    fun getGradesSummary(semesterId: Int?): Single<List<GradeSummary>> {
        return api.getGradesSummary(semesterId).map { res ->
            res.subjects.asSequence().map { summary ->
                summary.apply {
                    predicted = if (predicted != "-") getGradeShortValue(predicted) else ""
                    final = if (final != "-") getGradeShortValue(final) else ""
                }
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

    fun getHomework(startDate: LocalDate, endDate: LocalDate? = null): Single<List<Homework>> {
        val end = endDate ?: startDate.plusDays(4)
        return api.getHomework(startDate.toTick()).map { res ->
            res.items.asSequence().map {
                it.apply {
                    date = res.date
                }
            }.filter {
                it.date.toLocalDate() >= startDate && it.date.toLocalDate() <= end
            }.sortedWith(compareBy({ it.date }, { it.subject })).toList()
        }
    }

    fun getNotes(): Single<List<Note>> {
        return api.getNotes().map { res ->
            res.notes.asSequence().mapIndexed { i, note ->
                note.apply {
                    if (teacher == teacherSymbol) teacherSymbol = ""
                    date = res.dates[i]
                }
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
                    teacher.split(" [").run {
                        Teacher(first(), last().removeSuffix("]"), subject.name)
                    }
                }
            }.sortedWith(compareBy({ it.subject }, { it.name }))
        }
    }

    fun getSchool(): Single<School> {
        return api.getSchoolAndTeachers().map {
            School(it.name, it.address, it.contact, it.headmaster, it.pedagogue)
        }
    }

    fun getStudentInfo(): Single<StudentInfo> {
        return api.getStudentInfo().map {
            it.apply {
                student.polishCitizenship = if ("Tak" == student.polishCitizenship) "1" else "0"
            }
        }
    }

    fun getTimetable(startDate: LocalDate, endDate: LocalDate? = null): Single<List<Timetable>> {
        return api.getTimetable(startDate.getLastMonday().toTick()).map { res ->
            res.rows.flatMap { row ->
                row.lessons.asSequence().mapIndexed { i, it ->
                    it.apply {
                        date = res.days[i]
                        start = "${date.toLocalDate().toFormat("yyy-MM-dd")} ${row.startTime}".toDate("yyyy-MM-dd HH:mm")
                        end = "${date.toLocalDate().toFormat("yyy-MM-dd")} ${row.endTime}".toDate("yyyy-MM-dd HH:mm")
                        number = row.number
                    }
                }.mapNotNull { TimetableParser().getTimetable(it) }.toList()
            }.asSequence().filter {
                it.date.toLocalDate() >= startDate && it.date.toLocalDate() <= endDate ?: startDate.plusDays(4)
            }.sortedWith(compareBy({ it.date }, { it.number })).toList()
        }
    }

    fun getRealized(startDate: LocalDate?): Single<List<Realized>> {
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

    private fun LocalDate?.toTick() = this?.toDate().toTick()

    private fun Date?.toTick(): String {
        if (this == null) return ""
        val c = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeZone = TimeZone.getDefault()
            time = this@toTick
        }
        val utcOffset = c.get(Calendar.ZONE_OFFSET) + c.get(Calendar.DST_OFFSET)
        return ((c.timeInMillis + utcOffset) * 10000 + 621355968000000000L).toString()
    }

    private fun romanToMonthEnum(romanMonth: String): Month {
        return Month.of(when (romanMonth) {
            "I" -> 1
            "II" -> 2
            "III" -> 3
            "IV" -> 4
            "V" -> 5
            "VI" -> 6
            "VII" -> 7
            "VIII" -> 8
            "IX" -> 9
            "X" -> 10
            "XI" -> 11
            "XII" -> 12
            else -> 0
        })
    }
}

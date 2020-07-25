package io.github.wulkanowy.sdk.scrapper.repository

import io.github.wulkanowy.sdk.scrapper.attendance.Attendance
import io.github.wulkanowy.sdk.scrapper.attendance.Attendance.Types
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceSummary
import io.github.wulkanowy.sdk.scrapper.attendance.Subject
import io.github.wulkanowy.sdk.scrapper.exams.Exam
import io.github.wulkanowy.sdk.scrapper.getEmptyIfDash
import io.github.wulkanowy.sdk.scrapper.getGradeShortValue
import io.github.wulkanowy.sdk.scrapper.getLastMonday
import io.github.wulkanowy.sdk.scrapper.grades.Grade
import io.github.wulkanowy.sdk.scrapper.grades.GradeStatistics
import io.github.wulkanowy.sdk.scrapper.grades.GradeSummary
import io.github.wulkanowy.sdk.scrapper.homework.Homework
import io.github.wulkanowy.sdk.scrapper.mobile.Device
import io.github.wulkanowy.sdk.scrapper.mobile.TokenResponse
import io.github.wulkanowy.sdk.scrapper.notes.Note
import io.github.wulkanowy.sdk.scrapper.school.School
import io.github.wulkanowy.sdk.scrapper.school.Teacher
import io.github.wulkanowy.sdk.scrapper.service.StudentAndParentService
import io.github.wulkanowy.sdk.scrapper.student.StudentInfo
import io.github.wulkanowy.sdk.scrapper.timetable.CompletedLesson
import io.github.wulkanowy.sdk.scrapper.timetable.Timetable
import io.github.wulkanowy.sdk.scrapper.timetable.TimetableParser
import io.github.wulkanowy.sdk.scrapper.toDate
import io.github.wulkanowy.sdk.scrapper.toFormat
import io.github.wulkanowy.sdk.scrapper.toLocalDate
import java.time.LocalDate
import java.time.Month
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

class StudentAndParentRepository(private val api: StudentAndParentService) {

    suspend fun getAttendance(startDate: LocalDate, endDate: LocalDate? = null): List<Attendance> {
        val end = endDate ?: startDate.plusDays(4)
        val res = api.getAttendance(startDate.getLastMonday().toTick())
        return res.rows.flatMap { row ->
            row.lessons.mapIndexedNotNull { i, it ->
                if ("null" == it.subject) return@mapIndexedNotNull null // fix empty months
                it.apply {
                    date = res.days[i]
                    number = row.number
                    presence = it.type == Types.PRESENCE || it.type == Types.ABSENCE_FOR_SCHOOL_REASONS
                    absence = it.type == Types.ABSENCE_UNEXCUSED || it.type == Types.ABSENCE_EXCUSED
                    lateness = it.type == Types.EXCUSED_LATENESS || it.type == Types.UNEXCUSED_LATENESS
                    excused = it.type == Types.ABSENCE_EXCUSED || it.type == Types.EXCUSED_LATENESS
                    exemption = it.type == Types.EXEMPTION
                    excusable = res.excuseActiveSnp.isNotEmpty() && (absence || lateness) && !excused
                }
            }
        }.asSequence().filter {
            it.date.toLocalDate() >= startDate && it.date.toLocalDate() <= end
        }.sortedWith(compareBy({ it.date }, { it.number })).toList()
    }

    suspend fun getAttendanceSummary(subjectId: Int?): List<AttendanceSummary> {
        val res = api.getAttendanceSummary(subjectId)
        return res.months.mapIndexedNotNull { i, month ->
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

    suspend fun getSubjects(): List<Subject> {
        return api.getAttendanceSummary(-1).subjects
    }

    suspend fun getExams(startDate: LocalDate, endDate: LocalDate? = null): List<Exam> {
        val end = endDate ?: startDate.plusDays(4)
        val res = api.getExams(startDate.getLastMonday().toTick())
        return res.days.flatMap { day ->
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

    suspend fun getGrades(semesterId: Int?): Pair<List<Grade>, List<GradeSummary>> {
        return getGradesDetails(semesterId) to getGradesSummary(semesterId)
    }

    suspend fun getGradesDetails(semesterId: Int?): List<Grade> {
        val res = api.getGrades(semesterId)
        return res.grades.asSequence().map { grade ->
            grade.apply {
                comment = if (entry.length > 4) "$entry ($comment)".replace(" ($comment)", "") else comment
                entry = entry.run { if (length > 4) "..." else this }

                if (entry == comment) comment = ""
                if (description == symbol) description = ""
            }
        }.toList().sortedByDescending { it.date }
    }

    suspend fun getGradesSummary(semesterId: Int?): List<GradeSummary> {
        val res = api.getGradesSummary(semesterId)
        return res.subjects.asSequence().map { summary ->
            summary.apply {
                predicted = if (predicted != "-") getGradeShortValue(predicted) else ""
                final = if (final != "-") getGradeShortValue(final) else ""
            }
        }.sortedBy { it.name }.toList()
    }

    suspend fun getGradesStatistics(semesterId: Int, annual: Boolean): List<GradeStatistics> {
        val res = api.getGradesStatistics(if (!annual) 1 else 2, semesterId)
        return res.items.map {
            it.apply {
                grade = getGradeShortValue(grade)
                gradeValue = getGradeShortValue(grade).toIntOrNull() ?: 0
                this.semesterId = res.semesterId
            }
        }
    }

    suspend fun getHomework(startDate: LocalDate, endDate: LocalDate? = null): List<Homework> {
        val end = endDate ?: startDate.plusDays(4)
        val res = api.getHomework(startDate.toTick())
        return res.items.asSequence().map {
            it.apply {
                date = res.date
            }
        }.filter {
            it.date.toLocalDate() >= startDate && it.date.toLocalDate() <= end
        }.sortedWith(compareBy({ it.date }, { it.subject })).toList()
    }

    suspend fun getNotes(): List<Note> {
        val res = api.getNotes()
        return res.notes.asSequence().mapIndexed { i, note ->
            note.apply {
                if (teacher == teacherSymbol) teacherSymbol = ""
                date = res.dates[i]
            }
        }.sortedWith(compareBy({ it.date }, { it.category })).toList()
    }

    suspend fun getRegisteredDevices(): List<Device> {
        return api.getRegisteredDevices().devices
    }

    suspend fun getToken(): TokenResponse {
        return api.getToken()
    }

    suspend fun unregisterDevice(id: Int): Boolean {
        return api.unregisterDevice(id).devices.any { device -> device.id == id }
    }

    suspend fun getTeachers(): List<Teacher> {
        val res = api.getSchoolAndTeachers()
        return res.subjects.flatMap { subject ->
            subject.teachers.split(", ").map { teacher ->
                teacher.split(" [").run {
                    Teacher(first().getEmptyIfDash(), last().removeSuffix("]").getEmptyIfDash(), subject.name.let {
                        if (it.isBlank()) ""
                        else it
                    })
                }
            }
        }.sortedWith(compareBy({ it.subject }, { it.name }))
    }

    suspend fun getSchool(): School {
        val res = api.getSchoolAndTeachers()
        return School(res.name, res.address, res.contact, res.headmaster, res.pedagogue)
    }

    suspend fun getStudentInfo(): StudentInfo {
        return api.getStudentInfo().apply {
            student.polishCitizenship = if ("Tak" == student.polishCitizenship) "1" else "0"
        }
    }

    suspend fun getTimetable(startDate: LocalDate, endDate: LocalDate? = null): List<Timetable> {
        val res = api.getTimetable(startDate.getLastMonday().toTick())
        return res.rows.flatMap { row ->
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

    suspend fun getCompletedLessons(start: LocalDate, endDate: LocalDate?, subjectId: Int): List<CompletedLesson> {
        val end = endDate ?: start.plusMonths(1)
        val res = api.getCompletedLessons(start.toTick(), end.toTick(), subjectId)
        return res.items.mapNotNull {
            lateinit var lastDate: Date
            if (it.subject.isBlank()) {
                lastDate = it.date
                return@mapNotNull null
            }

            it.apply { date = lastDate }
        }.sortedWith(compareBy({ it.date }, { it.number })).filter {
            it.date.toLocalDate() >= start && it.date.toLocalDate() <= end
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

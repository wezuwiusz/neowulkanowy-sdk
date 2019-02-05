package io.github.wulkanowy.api.repository

import io.github.wulkanowy.api.attendance.Attendance
import io.github.wulkanowy.api.attendance.AttendanceRequest
import io.github.wulkanowy.api.attendance.AttendanceSummary
import io.github.wulkanowy.api.attendance.AttendanceSummaryRequest
import io.github.wulkanowy.api.attendance.Subject
import io.github.wulkanowy.api.exams.Exam
import io.github.wulkanowy.api.exams.ExamRequest
import io.github.wulkanowy.api.getGradeShortValue
import io.github.wulkanowy.api.getSchoolYear
import io.github.wulkanowy.api.grades.Grade
import io.github.wulkanowy.api.grades.GradeRequest
import io.github.wulkanowy.api.grades.GradeSummary
import io.github.wulkanowy.api.grades.getGradeValueWithModifier
import io.github.wulkanowy.api.grades.isGradeValid
import io.github.wulkanowy.api.homework.Homework
import io.github.wulkanowy.api.mobile.Device
import io.github.wulkanowy.api.notes.Note
import io.github.wulkanowy.api.school.School
import io.github.wulkanowy.api.school.Teacher
import io.github.wulkanowy.api.service.StudentService
import io.github.wulkanowy.api.timetable.CacheResponse
import io.github.wulkanowy.api.timetable.Timetable
import io.github.wulkanowy.api.timetable.TimetableParser
import io.github.wulkanowy.api.timetable.TimetableRequest
import io.github.wulkanowy.api.timetable.TimetableResponse
import io.github.wulkanowy.api.toDate
import io.github.wulkanowy.api.toFormat
import io.github.wulkanowy.api.toLocalDate
import io.reactivex.Observable
import io.reactivex.Single
import org.jsoup.Jsoup
import org.threeten.bp.LocalDate
import org.threeten.bp.Month

class StudentRepository(private val api: StudentService) {

    private lateinit var cache: CacheResponse

    private lateinit var times: List<CacheResponse.Time>

    private fun getCache(): Single<CacheResponse> {
        if (::cache.isInitialized) return Single.just(cache)

        return api.getStart("Start").flatMap {
            api.getUserCache(
                    getScriptParam("antiForgeryToken: '(.)*',".toRegex(), it),
                    getScriptParam("appGuid: '(.)*',".toRegex(), it),
                    getScriptParam("version: '(.)*',".toRegex(), it)
            )
        }.map { it.data }
    }

    private fun getTimes(): Single<List<CacheResponse.Time>> {
        if (::times.isInitialized) return Single.just(times)

        return getCache().map { res -> res.times }.map { list ->
            list.apply { times = this }
        }
    }

    private fun getScriptParam(regex: Regex, content: String): String {
        return regex.find(content).let { result ->
            if (null !== result) result.groupValues[0].substringAfter("'").substringBefore("'") else ""
        }
    }

    fun getAttendance(startDate: LocalDate, endDate: LocalDate? = null): Single<List<Attendance>> {
        val end = endDate ?: startDate.plusDays(4)
        return api.getAttendance(AttendanceRequest(startDate.toDate())).map { it.data?.lessons }
                .flatMapObservable { Observable.fromIterable(it) }
                .flatMap { a ->
                    getTimes().flatMapObservable { times ->
                        Observable.fromIterable(times.filter { time -> time.id == a.number })
                    }.map {
                        a.apply {
                            presence = a.categoryId == Attendance.Category.PRESENCE.id || a.categoryId == Attendance.Category.ABSENCE_FOR_SCHOOL_REASONS.id
                            absence = a.categoryId == Attendance.Category.ABSENCE_UNEXCUSED.id || a.categoryId == Attendance.Category.ABSENCE_EXCUSED.id
                            lateness = a.categoryId == Attendance.Category.EXCUSED_LATENESS.id || a.categoryId == Attendance.Category.UNEXCUSED_LATENESS.id
                            excused = a.categoryId == Attendance.Category.ABSENCE_EXCUSED.id || a.categoryId == Attendance.Category.EXCUSED_LATENESS.id
                            exemption = a.categoryId == Attendance.Category.EXEMPTION.id
                            name = (Attendance.Category.values()
                                    .singleOrNull { category -> category.id == categoryId }
                                    ?: Attendance.Category.UNKNOWN).title
                            number = it.number
                        }
                    }
                }.filter {
                    it.date.toLocalDate() >= startDate && it.date.toLocalDate() <= end
                }.toList().map { list -> list.sortedWith(compareBy({ it.date }, { it.number })) }
    }

    fun getAttendanceSummary(subjectId: Int?): Single<List<AttendanceSummary>> {
        return api.getAttendanceStatistics(AttendanceSummaryRequest(subjectId)).map { it.data?.items }.map {
            listOf(
                    AttendanceSummary(Month.SEPTEMBER, it[0].september, it[1].september, it[2].september, it[3].september, it[4].september, it[5].september, it[6].september),
                    AttendanceSummary(Month.OCTOBER, it[0].october, it[1].october, it[2].october, it[3].october, it[4].october, it[5].october, it[6].october),
                    AttendanceSummary(Month.NOVEMBER, it[0].november, it[1].november, it[2].november, it[3].november, it[4].november, it[5].november, it[6].november),
                    AttendanceSummary(Month.DECEMBER, it[0].december, it[1].december, it[2].december, it[3].december, it[4].december, it[5].december, it[6].december),
                    AttendanceSummary(Month.JANUARY, it[0].january, it[1].january, it[2].january, it[3].january, it[4].january, it[5].january, it[6].january),
                    AttendanceSummary(Month.FEBRUARY, it[0].february, it[1].february, it[2].february, it[3].february, it[4].february, it[5].february, it[6].february),
                    AttendanceSummary(Month.MARCH, it[0].march, it[1].march, it[2].march, it[3].march, it[4].march, it[5].march, it[6].march),
                    AttendanceSummary(Month.APRIL, it[0].april, it[1].april, it[2].april, it[3].april, it[4].april, it[5].april, it[6].april),
                    AttendanceSummary(Month.MAY, it[0].may, it[1].may, it[2].may, it[3].may, it[4].may, it[5].may, it[6].may),
                    AttendanceSummary(Month.JUNE, it[0].june, it[1].june, it[2].june, it[3].june, it[4].june, it[5].june, it[6].june)
            ).filterNot { summary ->
                summary.absence == 0 &&
                        summary.absenceExcused == 0 &&
                        summary.absenceForSchoolReasons == 0 &&
                        summary.exemption == 0 &&
                        summary.lateness == 0 &&
                        summary.latenessExcused == 0 &&
                        summary.presence == 0
            }
        }
    }

    fun getSubjects(): Single<List<Subject>> {
        return api.getAttendanceSubjects().map { it.data }
    }

    fun getExams(startDate: LocalDate, endDate: LocalDate? = null): Single<List<Exam>> {
        val end = endDate ?: startDate.plusDays(4)
        return api.getExams(ExamRequest(startDate.toDate(), startDate.getSchoolYear())).map { res ->
            res.data?.asSequence()?.map { weeks ->
                weeks.weeks.map { day ->
                    day.exams.map { exam ->
                        exam.apply {
                            group = subject.split("|").last()
                            subject = subject.substringBeforeLast(" ")
                            if (group.contains(" ")) group = ""
                            date = day.date
                            type = if ("1" == type) "Sprawdzian" else "Kartkówka"
                            teacherSymbol = teacher.split(" [").last().removeSuffix("]")
                            teacher = teacher.split(" [").first()
                        }
                    }
                }.flatten()
            }?.flatten()?.filter {
                it.date.toLocalDate() >= startDate && it.date.toLocalDate() <= end
            }?.sortedBy { it.date }?.toList()
        }
    }

    fun getGrades(semesterId: Int?): Single<List<Grade>> {
        return api.getGrades(GradeRequest(semesterId)).map { res ->
            res.data?.gradesWithSubjects?.map { subject ->
                subject.grades.map {
                    val values = getGradeValueWithModifier(it.entry)
                    it.apply {
                        this.subject = subject.name
                        comment = entry.substringAfter(" (").removeSuffix(")")
                        entry = entry.substringBefore(" (")
                        if (comment == entry) comment = ""
                        value = values.first
                        date = privateDate
                        modifier = values.second
                        weight = "$weightValue,00"
                        weightValue = if (isGradeValid(entry)) weightValue else 0
                        color = if ("0" == color) "000000" else color.toInt().toString(16).toUpperCase()
                        symbol = symbol ?: ""
                        description = description ?: ""
                    }
                }
            }?.flatten()?.sortedByDescending { it.date }
        }
    }

    fun getGradesSummary(semesterId: Int?): Single<List<GradeSummary>> {
        return api.getGrades(GradeRequest(semesterId)).map { res ->
            res.data?.gradesWithSubjects?.map { subject ->
                GradeSummary().apply {
                    name = subject.name
                    predicted = getGradeShortValue(subject.proposed)
                    final = getGradeShortValue(subject.annual)
                }
            }?.sortedBy { it.name }?.toList()
        }
    }

    fun getHomework(startDate: LocalDate, endDate: LocalDate? = null): Single<List<Homework>> {
        val end = endDate ?: startDate
        return api.getHomework(ExamRequest(startDate.toDate(), startDate.getSchoolYear())).map { res ->
            res.data?.asSequence()?.map { day ->
                day.items.map {
                    val teacherAndDate = it.teacher.split(", ")
                    it.apply {
                        date = day.date
                        entryDate = teacherAndDate.last().toDate("dd.MM.yyyy")
                        teacher = teacherAndDate.first().split(" [").first()
                        teacherSymbol = teacherAndDate.first().split(" [").last().removeSuffix("]")
                    }
                }
            }?.flatten()?.filter {
                it.date.toLocalDate() in startDate..end
            }?.sortedWith(compareBy({ it.date }, { it.subject }))?.toList()
        }
    }

    fun getNotes(): Single<List<Note>> {
        return api.getNotes().map { res ->
            res.data?.notes?.map {
                it.apply {
                    teacherSymbol = teacher.split(" [").last().removeSuffix("]")
                    teacher = teacher.split(" [").first()
                }
            }
        }
    }

    fun getTimetable(startDate: LocalDate, endDate: LocalDate? = null): Single<List<Timetable>> {
        return api.getTimetable(TimetableRequest(startDate.toFormat("yyyy-MM-dd'T00:00:00'"))).map { res ->
            res.data?.rows2api?.flatMap { lessons ->
                lessons.drop(1).mapIndexed { i, it ->
                    val times = lessons[0].split("<br />")
                    TimetableResponse.TimetableRow.TimetableCell().apply {
                        date = res.data.header.drop(1)[i].date.split("<br />")[1].toDate("dd.MM.yyyy")
                        start = "${date.toLocalDate().toFormat("yyyy-MM-dd")} ${times[1]}".toDate("yyyy-MM-dd HH:mm")
                        end = "${date.toLocalDate().toFormat("yyyy-MM-dd")} ${times[2]}".toDate("yyyy-MM-dd HH:mm")
                        number = times[0].toInt()
                        td = Jsoup.parse(it)
                    }
                }.mapNotNull { TimetableParser().getTimetable(it) }
            }?.asSequence()?.filter {
                it.date.toLocalDate() >= startDate && it.date.toLocalDate() <= endDate ?: startDate.plusDays(4)
            }?.sortedWith(compareBy({ it.date }, { it.number }))?.toList()
        }
    }

    fun getTeachers(): Single<List<Teacher>> {
        return api.getSchoolAndTeachers()
                .map { it.data }
                .map { res ->
                    res.teachers.map {
                        it.copy(
                                short = it.name.substringAfter("[").substringBefore("]"),
                                name = it.name.substringBefore(" [")
                        )
                    }
                }
    }

    fun getSchool(): Single<School> {
        return api.getSchoolAndTeachers()
                .map { it.data }
                .map { it.school }
    }

    fun getRegisteredDevices(): Single<List<Device>> {
        return api.getRegisteredDevices().map { it.data }
    }
}

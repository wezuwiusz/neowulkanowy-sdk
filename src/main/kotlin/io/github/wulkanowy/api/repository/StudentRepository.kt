package io.github.wulkanowy.api.repository

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import io.github.wulkanowy.api.ApiResponse
import io.github.wulkanowy.api.attendance.Absent
import io.github.wulkanowy.api.attendance.Attendance
import io.github.wulkanowy.api.attendance.Attendance.Category
import io.github.wulkanowy.api.attendance.AttendanceExcuseRequest
import io.github.wulkanowy.api.attendance.AttendanceRequest
import io.github.wulkanowy.api.attendance.AttendanceSummary
import io.github.wulkanowy.api.attendance.AttendanceSummaryItemSerializer
import io.github.wulkanowy.api.attendance.AttendanceSummaryRequest
import io.github.wulkanowy.api.attendance.AttendanceSummaryResponse
import io.github.wulkanowy.api.attendance.Subject
import io.github.wulkanowy.api.exams.Exam
import io.github.wulkanowy.api.exams.ExamRequest
import io.github.wulkanowy.api.getGradeShortValue
import io.github.wulkanowy.api.getSchoolYear
import io.github.wulkanowy.api.getScriptParam
import io.github.wulkanowy.api.grades.Grade
import io.github.wulkanowy.api.grades.GradeRequest
import io.github.wulkanowy.api.grades.GradeStatistics
import io.github.wulkanowy.api.grades.GradeSummary
import io.github.wulkanowy.api.grades.GradesStatisticsRequest
import io.github.wulkanowy.api.grades.getGradeValueWithModifier
import io.github.wulkanowy.api.grades.isGradeValid
import io.github.wulkanowy.api.homework.Homework
import io.github.wulkanowy.api.interceptor.FeatureDisabledException
import io.github.wulkanowy.api.interceptor.VulcanException
import io.github.wulkanowy.api.mobile.Device
import io.github.wulkanowy.api.mobile.TokenResponse
import io.github.wulkanowy.api.mobile.UnregisterDeviceRequest
import io.github.wulkanowy.api.notes.Note
import io.github.wulkanowy.api.school.School
import io.github.wulkanowy.api.school.Teacher
import io.github.wulkanowy.api.service.StudentService
import io.github.wulkanowy.api.timetable.CacheResponse
import io.github.wulkanowy.api.timetable.CompletedLesson
import io.github.wulkanowy.api.timetable.CompletedLessonsRequest
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
import java.lang.String.format
import java.util.Locale

class StudentRepository(private val api: StudentService) {

    private lateinit var cache: CacheResponse

    private lateinit var times: List<CacheResponse.Time>

    private val gson by lazy { GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss") }

    private fun LocalDate.toISOFormat(): String = toFormat("yyyy-MM-dd'T00:00:00'")

    private fun getCache(): Single<CacheResponse> {
        if (::cache.isInitialized) return Single.just(cache)

        return api.getStart("Start").flatMap {
            api.getUserCache(
                getScriptParam("antiForgeryToken", it),
                getScriptParam("appGuid", it),
                getScriptParam("version", it)
            )
        }.map { it.data }
    }

    private fun getTimes(): Single<List<CacheResponse.Time>> {
        if (::times.isInitialized) return Single.just(times)

        return getCache().map { res -> res.times }.map { list ->
            list.apply { times = this }
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
                        presence = a.categoryId == Category.PRESENCE.id || a.categoryId == Category.ABSENCE_FOR_SCHOOL_REASONS.id
                        absence = a.categoryId == Category.ABSENCE_UNEXCUSED.id || a.categoryId == Category.ABSENCE_EXCUSED.id
                        lateness = a.categoryId == Category.EXCUSED_LATENESS.id || a.categoryId == Category.UNEXCUSED_LATENESS.id
                        excused = a.categoryId == Category.ABSENCE_EXCUSED.id || a.categoryId == Category.EXCUSED_LATENESS.id
                        exemption = a.categoryId == Category.EXEMPTION.id
                        name = (Category.values().singleOrNull { category -> category.id == categoryId } ?: Category.UNKNOWN).title
                        number = it.number
                    }
                }
            }.filter {
                it.date.toLocalDate() >= startDate && it.date.toLocalDate() <= end
            }.toList().map { list -> list.sortedWith(compareBy({ it.date }, { it.number })) }
    }

    fun getAttendanceSummary(subjectId: Int?): Single<List<AttendanceSummary>> {
        return api.getAttendanceStatistics(AttendanceSummaryRequest(subjectId)).map { it.data }.map { res ->
            val stats = res.items.map {
                (gson.create().fromJson<LinkedTreeMap<String, String?>>(
                    gson.registerTypeAdapter(
                        AttendanceSummaryResponse.Summary::class.java,
                        AttendanceSummaryItemSerializer()
                    ).create().toJson(it), object : TypeToken<LinkedTreeMap<String, String?>>() {}.type
                ))
            }

            val getMonthValue = fun(type: Int, month: Int): Int {
                return stats[type][stats[0].keys.toTypedArray()[month + 1]]?.toInt() ?: 0
            }

            (1..12).map {
                AttendanceSummary(
                    Month.of(if (it < 5) 8 + it else it - 4),
                    getMonthValue(0, it), getMonthValue(1, it), getMonthValue(2, it), getMonthValue(3, it),
                    getMonthValue(4, it), getMonthValue(5, it), getMonthValue(6, it)
                )
            }.filterNot { summary ->
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

    fun excuseForAbsence(absents: List<Absent>, content: String?): Single<Boolean> {
        return api.excuseForAbsence(
            AttendanceExcuseRequest(
                AttendanceExcuseRequest.Excuse(
                    absents = absents.map {
                        AttendanceExcuseRequest.Excuse.Absent(
                            date = it.date.toFormat("yyyy-MM-dd'T'HH:mm:ss"),
                            timeId = it.timeId
                        )
                    },
                    content = content
                )
            )
        ).map { it.success }
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
                            type = when (type) {
                                "1" -> "Sprawdzian"
                                "2" -> "Kartkówka"
                                else -> "Praca klasowa"
                            }
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
            res.data?.gradesWithSubjects?.map { gradesSubject ->
                gradesSubject.grades.map { grade ->
                    val values = getGradeValueWithModifier(grade.entry)
                    grade.apply {
                        subject = gradesSubject.name
                        comment = entry.substringBefore(" (").run {
                            if (length > 4) this
                            else entry.substringBeforeLast(")").substringAfter(" (")
                        }
                        entry = entry.substringBefore(" (").run { if (length > 4) "..." else this }
                        if (comment == entry) comment = ""
                        value = values.first
                        date = privateDate
                        modifier = values.second
                        weight = format(Locale.FRANCE, "%.2f", weightValue)
                        weightValue = if (isGradeValid(entry)) weightValue else .0
                        color = if ("0" == color) "000000" else color.toInt().toString(16).toUpperCase()
                        symbol = symbol ?: ""
                        description = description ?: ""
                    }
                }
            }?.flatten()?.sortedByDescending { it.date }
        }
    }

    fun getGradesStatistics(semesterId: Int, annual: Boolean): Single<List<GradeStatistics>> {
        return if (annual) api.getGradesAnnualStatistics(GradesStatisticsRequest(semesterId)).map { it.data }.map {
            it.map { annualSubject ->
                annualSubject.items?.reversed()?.mapIndexed { index, item ->
                    item.apply {
                        this.semesterId = semesterId
                        gradeValue = index + 1
                        grade = item.gradeValue.toString()
                        subject = annualSubject.subject
                    }
                }.orEmpty()
            }.flatten().reversed()
        } else return api.getGradesPartialStatistics(GradesStatisticsRequest(semesterId)).map { it.data }.map {
            it.map { partialSubject ->
                partialSubject.classSeries.items?.reversed()?.mapIndexed { index, item ->
                    item.apply {
                        this.semesterId = semesterId
                        gradeValue = index + 1
                        grade = item.gradeValue.toString()
                        subject = partialSubject.subject
                    }
                }?.reversed().orEmpty()
            }.flatten()
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
            }?.sortedWith(compareBy({ it.date }, { it.category }))
        }
    }

    fun getTimetable(startDate: LocalDate, endDate: LocalDate? = null): Single<List<Timetable>> {
        return api.getTimetable(TimetableRequest(startDate.toISOFormat())).map { res ->
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

    fun getCompletedLessons(start: LocalDate, endDate: LocalDate?, subjectId: Int): Single<List<CompletedLesson>> {
        val end = endDate ?: start.plusMonths(1)
        return api.getCompletedLessons(CompletedLessonsRequest(start.toISOFormat(), end.toISOFormat(), subjectId)).map {
            gson.create().fromJson(it, ApiResponse::class.java)
        }.map { res ->
            if (!res.success) throw FeatureDisabledException(res.feedback.message)
            (res.data as LinkedTreeMap<*, *>).map { list ->
                gson.create().fromJson<List<CompletedLesson>>(Gson().toJson(list.value), object : TypeToken<ArrayList<CompletedLesson>>() {}.type)
            }.flatten().map {
                it.apply {
                    teacherSymbol = teacher.substringAfter(" [").substringBefore("]")
                    teacher = teacher.substringBefore(" [")
                }
            }.sortedWith(compareBy({ it.date }, { it.number })).toList().filter {
                it.date.toLocalDate() >= start && it.date.toLocalDate() <= end
            }
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
                }.sortedWith(compareBy({ it.subject }, { it.name }))
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

    fun getToken(): Single<TokenResponse> {
        return api.getToken().map { it.data }
    }

    fun unregisterDevice(id: Int): Single<Boolean> {
        return api.getStart("Start").flatMap {
            api.unregisterDevice(
                getScriptParam("antiForgeryToken", it),
                getScriptParam("appGuid", it),
                getScriptParam("version", it),
                UnregisterDeviceRequest(id)
            )
        }.map {
            if (!it.success) throw VulcanException(it.feedback.message)
            it.success
        }
    }
}

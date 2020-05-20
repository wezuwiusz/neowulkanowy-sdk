package io.github.wulkanowy.sdk.scrapper.repository

import com.google.gson.GsonBuilder
import io.github.wulkanowy.sdk.scrapper.ApiResponse
import io.github.wulkanowy.sdk.scrapper.attendance.Absent
import io.github.wulkanowy.sdk.scrapper.attendance.Attendance
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceExcuseRequest
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceRequest
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceSummary
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceSummaryRequest
import io.github.wulkanowy.sdk.scrapper.attendance.Subject
import io.github.wulkanowy.sdk.scrapper.attendance.mapAttendanceList
import io.github.wulkanowy.sdk.scrapper.attendance.mapAttendanceSummaryList
import io.github.wulkanowy.sdk.scrapper.exams.Exam
import io.github.wulkanowy.sdk.scrapper.exams.ExamRequest
import io.github.wulkanowy.sdk.scrapper.exams.mapExamsList
import io.github.wulkanowy.sdk.scrapper.exception.InvalidPathException
import io.github.wulkanowy.sdk.scrapper.getSchoolYear
import io.github.wulkanowy.sdk.scrapper.getScriptParam
import io.github.wulkanowy.sdk.scrapper.grades.Grade
import io.github.wulkanowy.sdk.scrapper.grades.GradePointsSummary
import io.github.wulkanowy.sdk.scrapper.grades.GradeRequest
import io.github.wulkanowy.sdk.scrapper.grades.GradeStatistics
import io.github.wulkanowy.sdk.scrapper.grades.GradeSummary
import io.github.wulkanowy.sdk.scrapper.grades.GradesStatisticsRequest
import io.github.wulkanowy.sdk.scrapper.grades.mapGradesList
import io.github.wulkanowy.sdk.scrapper.grades.mapGradesStatisticsAnnual
import io.github.wulkanowy.sdk.scrapper.grades.mapGradesStatisticsPartial
import io.github.wulkanowy.sdk.scrapper.grades.mapGradesStatisticsPoints
import io.github.wulkanowy.sdk.scrapper.grades.mapGradesSummary
import io.github.wulkanowy.sdk.scrapper.homework.Homework
import io.github.wulkanowy.sdk.scrapper.homework.HomeworkRequest
import io.github.wulkanowy.sdk.scrapper.homework.mapHomework
import io.github.wulkanowy.sdk.scrapper.homework.mapHomeworkList
import io.github.wulkanowy.sdk.scrapper.interceptor.ErrorHandlerTransformer
import io.github.wulkanowy.sdk.scrapper.interceptor.FeatureDisabledException
import io.github.wulkanowy.sdk.scrapper.mobile.Device
import io.github.wulkanowy.sdk.scrapper.mobile.TokenResponse
import io.github.wulkanowy.sdk.scrapper.mobile.UnregisterDeviceRequest
import io.github.wulkanowy.sdk.scrapper.notes.Note
import io.github.wulkanowy.sdk.scrapper.school.School
import io.github.wulkanowy.sdk.scrapper.school.Teacher
import io.github.wulkanowy.sdk.scrapper.school.mapToTeachers
import io.github.wulkanowy.sdk.scrapper.service.StudentService
import io.github.wulkanowy.sdk.scrapper.timetable.CacheResponse
import io.github.wulkanowy.sdk.scrapper.timetable.CompletedLesson
import io.github.wulkanowy.sdk.scrapper.timetable.CompletedLessonsRequest
import io.github.wulkanowy.sdk.scrapper.timetable.Timetable
import io.github.wulkanowy.sdk.scrapper.timetable.TimetableRequest
import io.github.wulkanowy.sdk.scrapper.timetable.mapCompletedLessonsList
import io.github.wulkanowy.sdk.scrapper.timetable.mapTimetableList
import io.github.wulkanowy.sdk.scrapper.toDate
import io.github.wulkanowy.sdk.scrapper.toFormat
import io.reactivex.Single
import org.jsoup.Jsoup
import org.threeten.bp.LocalDate

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
        }.compose(ErrorHandlerTransformer()).map { requireNotNull(it.data) }
            .map { it.apply { cache = this } }
    }

    private fun getTimes(): Single<List<CacheResponse.Time>> {
        if (::times.isInitialized) return Single.just(times)

        return getCache().map { res -> res.times }.map { list ->
            list.apply { times = this }
        }
    }

    fun getAttendance(startDate: LocalDate, endDate: LocalDate?): Single<List<Attendance>> {
        return api.getAttendance(AttendanceRequest(startDate.toDate()))
            .compose(ErrorHandlerTransformer()).map { requireNotNull(it.data) }
            .mapAttendanceList(startDate, endDate, ::getTimes)
    }

    fun getAttendanceSummary(subjectId: Int?): Single<List<AttendanceSummary>> {
        return api.getAttendanceStatistics(AttendanceSummaryRequest(subjectId))
            .compose(ErrorHandlerTransformer()).map { requireNotNull(it.data) }
            .map { it.mapAttendanceSummaryList(gson) }
    }

    fun excuseForAbsence(absents: List<Absent>, content: String?): Single<Boolean> {
        return api.getStart("Start").flatMap {
            api.excuseForAbsence(
                getScriptParam("antiForgeryToken", it),
                getScriptParam("appGuid", it),
                getScriptParam("version", it),
                AttendanceExcuseRequest(
                    AttendanceExcuseRequest.Excuse(
                        absents = absents.map { absence ->
                            AttendanceExcuseRequest.Excuse.Absent(
                                date = absence.date.toFormat("yyyy-MM-dd'T'HH:mm:ss"),
                                timeId = absence.timeId
                            )
                        },
                        content = content
                    )
                )
            )
        }.compose(ErrorHandlerTransformer()).map { it.success }
    }

    fun getSubjects(): Single<List<Subject>> {
        return api.getAttendanceSubjects()
            .compose(ErrorHandlerTransformer()).map { it.data.orEmpty() }
    }

    fun getExams(startDate: LocalDate, endDate: LocalDate? = null): Single<List<Exam>> {
        return api.getExams(ExamRequest(startDate.toDate(), startDate.getSchoolYear()))
            .compose(ErrorHandlerTransformer()).map { it.data.orEmpty() }
            .map { it.mapExamsList(startDate, endDate) }
    }

    fun getGrades(semesterId: Int?): Single<Pair<List<Grade>, List<GradeSummary>>> {
        return api.getGrades(GradeRequest(semesterId))
            .compose(ErrorHandlerTransformer()).map { requireNotNull(it.data) }
            .map { it.mapGradesList() to it.mapGradesSummary() }
    }

    fun getGradesDetails(semesterId: Int?): Single<List<Grade>> {
        return api.getGrades(GradeRequest(semesterId))
            .compose(ErrorHandlerTransformer()).map { requireNotNull(it.data) }
            .map { it.mapGradesList() }
    }

    fun getGradesSummary(semesterId: Int?): Single<List<GradeSummary>> {
        return api.getGrades(GradeRequest(semesterId))
            .compose(ErrorHandlerTransformer()).map { requireNotNull(it.data) }
            .map { it.mapGradesSummary() }
    }

    fun getGradesPartialStatistics(semesterId: Int): Single<List<GradeStatistics>> {
        return api.getGradesPartialStatistics(GradesStatisticsRequest(semesterId))
            .compose(ErrorHandlerTransformer()).map { it.data.orEmpty() }
            .map { it.mapGradesStatisticsPartial(semesterId) }
    }

    fun getGradesPointsStatistics(semesterId: Int): Single<List<GradePointsSummary>> {
        return api.getGradesPointsStatistics(GradesStatisticsRequest(semesterId))
            .compose(ErrorHandlerTransformer()).map { it.data.orEmpty() }
            .map { it.mapGradesStatisticsPoints(semesterId) }
    }

    fun getGradesAnnualStatistics(semesterId: Int): Single<List<GradeStatistics>> {
        return api.getGradesAnnualStatistics(GradesStatisticsRequest(semesterId))
            .compose(ErrorHandlerTransformer()).map { it.data.orEmpty() }
            .map { it.mapGradesStatisticsAnnual(semesterId) }
    }

    fun getHomework(startDate: LocalDate, endDate: LocalDate? = null): Single<List<Homework>> {
        return api.getHomework(HomeworkRequest(startDate.toDate(), startDate.getSchoolYear(), -1))
            .compose(ErrorHandlerTransformer())
            .map { it.data.orEmpty().mapHomework(startDate, endDate) }
            .onErrorResumeNext { t ->
                if (t is InvalidPathException) api.getZadaniaDomowe(ExamRequest(startDate.toDate(), startDate.getSchoolYear()))
                    .compose(ErrorHandlerTransformer())
                    .map { it.data.orEmpty().mapHomeworkList(startDate, endDate) }
                else Single.error(t)
            }
    }

    fun getNotes(): Single<List<Note>> {
        return api.getNotes()
            .compose(ErrorHandlerTransformer()).map { it.data?.notes.orEmpty() }
            .map { res ->
                res.map {
                    it.apply {
                        teacherSymbol = teacher.split(" [").last().removeSuffix("]")
                        teacher = teacher.split(" [").first()
                    }
                }.sortedWith(compareBy({ it.date }, { it.category }))
            }
    }

    fun getTimetable(startDate: LocalDate, endDate: LocalDate? = null): Single<List<Timetable>> {
        return api.getTimetable(TimetableRequest(startDate.toISOFormat()))
            .compose(ErrorHandlerTransformer()).map { requireNotNull(it.data) }
            .map { it.mapTimetableList(startDate, endDate) }
    }

    fun getCompletedLessons(start: LocalDate, endDate: LocalDate?, subjectId: Int): Single<List<CompletedLesson>> {
        val end = endDate ?: start.plusMonths(1)
        return getCache().map { cache ->
            if (!cache.showCompletedLessons) throw FeatureDisabledException("Widok lekcji zrealizowanych został wyłączony przez Administratora szkoły")
        }.flatMap {
            api.getCompletedLessons(CompletedLessonsRequest(start.toISOFormat(), end.toISOFormat(), subjectId))
        }.map {
            gson.create().fromJson(it, ApiResponse::class.java)
        }.compose<ApiResponse<*>>(ErrorHandlerTransformer()).map { it.mapCompletedLessonsList(start, endDate, gson) }
    }

    fun getTeachers(): Single<List<Teacher>> {
        return api.getSchoolAndTeachers()
            .compose(ErrorHandlerTransformer()).map { requireNotNull(it.data) }
            .map { it.mapToTeachers() }
    }

    fun getSchool(): Single<School> {
        return api.getSchoolAndTeachers()
            .compose(ErrorHandlerTransformer()).map { requireNotNull(it.data) }
            .map { it.school }
    }

    fun getRegisteredDevices(): Single<List<Device>> {
        return api.getRegisteredDevices()
            .compose(ErrorHandlerTransformer()).map { it.data.orEmpty() }
    }

    fun getToken(): Single<TokenResponse> {
        return api.getToken()
            .compose(ErrorHandlerTransformer()).map { requireNotNull(it.data) }
            .map { res ->
                res.apply {
                    qrCodeImage = Jsoup.parse(qrCodeImage).select("img").attr("src").split("data:image/png;base64,")[1]
                }
            }
    }

    fun unregisterDevice(id: Int): Single<Boolean> {
        return api.getStart("Start").flatMap {
            api.unregisterDevice(
                getScriptParam("antiForgeryToken", it),
                getScriptParam("appGuid", it),
                getScriptParam("version", it),
                UnregisterDeviceRequest(id)
            )
        }.compose(ErrorHandlerTransformer()).map { it.success }
    }
}

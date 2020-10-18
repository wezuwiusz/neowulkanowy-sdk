package io.github.wulkanowy.sdk.scrapper.repository

import com.squareup.moshi.Moshi
import io.github.wulkanowy.sdk.scrapper.ApiResponseJsonAdapter
import io.github.wulkanowy.sdk.scrapper.adapter.CustomDateAdapter
import io.github.wulkanowy.sdk.scrapper.attendance.Absent
import io.github.wulkanowy.sdk.scrapper.attendance.Attendance
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceExcuseRequest
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceRequest
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceSummary
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceSummaryRequest
import io.github.wulkanowy.sdk.scrapper.attendance.Subject
import io.github.wulkanowy.sdk.scrapper.attendance.mapAttendanceList
import io.github.wulkanowy.sdk.scrapper.attendance.mapAttendanceSummaryList
import io.github.wulkanowy.sdk.scrapper.conferences.Conference
import io.github.wulkanowy.sdk.scrapper.conferences.mapConferences
import io.github.wulkanowy.sdk.scrapper.exams.Exam
import io.github.wulkanowy.sdk.scrapper.exams.ExamRequest
import io.github.wulkanowy.sdk.scrapper.exams.mapExamsList
import io.github.wulkanowy.sdk.scrapper.exception.FeatureDisabledException
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
import io.github.wulkanowy.sdk.scrapper.interceptor.handleErrors
import io.github.wulkanowy.sdk.scrapper.mobile.Device
import io.github.wulkanowy.sdk.scrapper.mobile.TokenResponse
import io.github.wulkanowy.sdk.scrapper.mobile.UnregisterDeviceRequest
import io.github.wulkanowy.sdk.scrapper.notes.Note
import io.github.wulkanowy.sdk.scrapper.school.School
import io.github.wulkanowy.sdk.scrapper.school.Teacher
import io.github.wulkanowy.sdk.scrapper.school.mapToTeachers
import io.github.wulkanowy.sdk.scrapper.service.StudentService
import io.github.wulkanowy.sdk.scrapper.student.StudentInfo
import io.github.wulkanowy.sdk.scrapper.timetable.CacheResponse
import io.github.wulkanowy.sdk.scrapper.timetable.CompletedLesson
import io.github.wulkanowy.sdk.scrapper.timetable.CompletedLessonsRequest
import io.github.wulkanowy.sdk.scrapper.timetable.Timetable
import io.github.wulkanowy.sdk.scrapper.timetable.TimetableRequest
import io.github.wulkanowy.sdk.scrapper.timetable.mapCompletedLessonsList
import io.github.wulkanowy.sdk.scrapper.timetable.mapTimetableList
import io.github.wulkanowy.sdk.scrapper.toDate
import io.github.wulkanowy.sdk.scrapper.toFormat
import org.jsoup.Jsoup
import java.time.LocalDate

class StudentRepository(private val api: StudentService) {

    private lateinit var cache: CacheResponse

    private lateinit var times: List<CacheResponse.Time>

    private val moshi by lazy { Moshi.Builder().add(CustomDateAdapter()) }

    private fun LocalDate.toISOFormat(): String = toFormat("yyyy-MM-dd'T00:00:00'")

    private suspend fun getCache(): CacheResponse {
        if (::cache.isInitialized) return cache

        val it = api.getStart("Start")

        val res = api.getUserCache(
            getScriptParam("antiForgeryToken", it),
            getScriptParam("appGuid", it),
            getScriptParam("version", it)
        ).handleErrors()

        val data = requireNotNull(res.data)
        cache = data
        return data
    }

    private suspend fun getTimes(): List<CacheResponse.Time> {
        if (::times.isInitialized) return times

        val res = getCache()
        times = res.times
        return res.times
    }

    suspend fun getAttendance(startDate: LocalDate, endDate: LocalDate?): List<Attendance> {
        return api.getAttendance(AttendanceRequest(startDate.toDate()))
            .handleErrors()
            .data?.mapAttendanceList(startDate, endDate, getTimes()).orEmpty()
    }

    suspend fun getAttendanceSummary(subjectId: Int?): List<AttendanceSummary> {
        return api.getAttendanceStatistics(AttendanceSummaryRequest(subjectId))
            .handleErrors()
            .data?.mapAttendanceSummaryList(moshi).orEmpty()
    }

    suspend fun excuseForAbsence(absents: List<Absent>, content: String?): Boolean {
        val it = api.getStart("Start")
        return api.excuseForAbsence(
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
        ).handleErrors().success
    }

    suspend fun getSubjects(): List<Subject> {
        return api.getAttendanceSubjects().handleErrors().data.orEmpty()
    }

    suspend fun getExams(startDate: LocalDate, endDate: LocalDate? = null): List<Exam> {
        return api.getExams(ExamRequest(startDate.toDate(), startDate.getSchoolYear()))
            .handleErrors()
            .data.orEmpty().mapExamsList(startDate, endDate)
    }

    suspend fun getGrades(semesterId: Int?): Pair<List<Grade>, List<GradeSummary>> {
        val data = api.getGrades(GradeRequest(semesterId))
            .handleErrors()
            .data
        return requireNotNull(data).mapGradesList() to data.mapGradesSummary()
    }

    suspend fun getGradesDetails(semesterId: Int?): List<Grade> {
        return api.getGrades(GradeRequest(semesterId))
            .handleErrors()
            .data?.mapGradesList().orEmpty()
    }

    suspend fun getGradesSummary(semesterId: Int?): List<GradeSummary> {
        return api.getGrades(GradeRequest(semesterId))
            .handleErrors()
            .data?.mapGradesSummary().orEmpty()
    }

    suspend fun getGradesPartialStatistics(semesterId: Int): List<GradeStatistics> {
        return api.getGradesPartialStatistics(GradesStatisticsRequest(semesterId))
            .handleErrors()
            .data.orEmpty().mapGradesStatisticsPartial(semesterId)
    }

    suspend fun getGradesPointsStatistics(semesterId: Int): List<GradePointsSummary> {
        return api.getGradesPointsStatistics(GradesStatisticsRequest(semesterId))
            .handleErrors()
            .data?.items.orEmpty().mapGradesStatisticsPoints(semesterId)
    }

    suspend fun getGradesAnnualStatistics(semesterId: Int): List<GradeStatistics> {
        return api.getGradesAnnualStatistics(GradesStatisticsRequest(semesterId))
            .handleErrors()
            .data.orEmpty().mapGradesStatisticsAnnual(semesterId)
    }

    suspend fun getHomework(startDate: LocalDate, endDate: LocalDate? = null): List<Homework> {
        return api.getHomework(HomeworkRequest(startDate.toDate(), startDate.getSchoolYear(), -1))
            .handleErrors()
            .data.orEmpty().mapHomework(startDate, endDate)
    }

    suspend fun getNotes(): List<Note> {
        return api.getNotes().handleErrors().data?.notes.orEmpty().map {
            it.copy(
                teacher = it.teacher.split(" [").first()
            ).apply {
                teacherSymbol = it.teacher.split(" [").last().removeSuffix("]")
            }
        }.sortedWith(compareBy({ it.date }, { it.category }))
    }

    suspend fun getConferences(): List<Conference> {
        return api.getConferences()
            .handleErrors().data.orEmpty()
            .mapConferences()
    }

    suspend fun getTimetable(startDate: LocalDate, endDate: LocalDate? = null): List<Timetable> {
        return api.getTimetable(TimetableRequest(startDate.toISOFormat())).handleErrors().data
            ?.mapTimetableList(startDate, endDate).orEmpty()
    }

    suspend fun getCompletedLessons(start: LocalDate, endDate: LocalDate?, subjectId: Int): List<CompletedLesson> {
        val end = endDate ?: start.plusMonths(1)
        val cache = getCache()
        if (!cache.showCompletedLessons) throw FeatureDisabledException("Widok lekcji zrealizowanych został wyłączony przez Administratora szkoły")

        val res = api.getCompletedLessons(CompletedLessonsRequest(start.toISOFormat(), end.toISOFormat(), subjectId))

        val adapter = ApiResponseJsonAdapter<Any>(moshi.build(), arrayOf(Any::class.java))
        return adapter.fromJson(res)?.handleErrors()?.mapCompletedLessonsList(start, endDate, moshi).orEmpty()
    }

    suspend fun getTeachers(): List<Teacher> {
        return api.getSchoolAndTeachers().handleErrors().data?.mapToTeachers().orEmpty()
    }

    suspend fun getSchool(): School {
        return api.getSchoolAndTeachers().handleErrors().data?.school!!
    }

    suspend fun getStudentInfo(): StudentInfo {
        // return api.getStudentInfo()
        return StudentInfo()
    }

    suspend fun getRegisteredDevices(): List<Device> {
        return api.getRegisteredDevices().handleErrors().data.orEmpty()
    }

    suspend fun getToken(): TokenResponse {
        val data = api.getToken().handleErrors().data
        return requireNotNull(data).copy(
            qrCodeImage = Jsoup.parse(data.qrCodeImage)
                .select("img")
                .attr("src")
                .split("data:image/png;base64,")[1]
        )
    }

    suspend fun unregisterDevice(id: Int): Boolean {
        val it = api.getStart("Start")
        return api.unregisterDevice(
            getScriptParam("antiForgeryToken", it),
            getScriptParam("appGuid", it),
            getScriptParam("version", it),
            UnregisterDeviceRequest(id)
        ).handleErrors().success
    }
}

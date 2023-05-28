package io.github.wulkanowy.sdk.scrapper.repository

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
import io.github.wulkanowy.sdk.scrapper.exception.ScrapperException
import io.github.wulkanowy.sdk.scrapper.getSchoolYear
import io.github.wulkanowy.sdk.scrapper.getScriptFlag
import io.github.wulkanowy.sdk.scrapper.getScriptParam
import io.github.wulkanowy.sdk.scrapper.grades.GradePointsSummary
import io.github.wulkanowy.sdk.scrapper.grades.GradeRequest
import io.github.wulkanowy.sdk.scrapper.grades.Grades
import io.github.wulkanowy.sdk.scrapper.grades.GradesStatisticsPartial
import io.github.wulkanowy.sdk.scrapper.grades.GradesStatisticsRequest
import io.github.wulkanowy.sdk.scrapper.grades.GradesStatisticsSemester
import io.github.wulkanowy.sdk.scrapper.grades.mapGradesList
import io.github.wulkanowy.sdk.scrapper.grades.mapGradesStatisticsPartial
import io.github.wulkanowy.sdk.scrapper.grades.mapGradesStatisticsSemester
import io.github.wulkanowy.sdk.scrapper.grades.mapGradesSummary
import io.github.wulkanowy.sdk.scrapper.homework.Homework
import io.github.wulkanowy.sdk.scrapper.homework.HomeworkRequest
import io.github.wulkanowy.sdk.scrapper.homework.mapHomework
import io.github.wulkanowy.sdk.scrapper.interceptor.handleErrors
import io.github.wulkanowy.sdk.scrapper.menu.Menu
import io.github.wulkanowy.sdk.scrapper.menu.MenuRequest
import io.github.wulkanowy.sdk.scrapper.mobile.Device
import io.github.wulkanowy.sdk.scrapper.mobile.TokenResponse
import io.github.wulkanowy.sdk.scrapper.mobile.UnregisterDeviceRequest
import io.github.wulkanowy.sdk.scrapper.notes.Note
import io.github.wulkanowy.sdk.scrapper.register.AuthorizePermission
import io.github.wulkanowy.sdk.scrapper.register.AuthorizePermissionRequest
import io.github.wulkanowy.sdk.scrapper.register.RegisterStudent
import io.github.wulkanowy.sdk.scrapper.register.getStudentsFromDiaries
import io.github.wulkanowy.sdk.scrapper.school.School
import io.github.wulkanowy.sdk.scrapper.school.Teacher
import io.github.wulkanowy.sdk.scrapper.school.mapToSchool
import io.github.wulkanowy.sdk.scrapper.school.mapToTeachers
import io.github.wulkanowy.sdk.scrapper.service.StudentPlusService
import io.github.wulkanowy.sdk.scrapper.service.StudentService
import io.github.wulkanowy.sdk.scrapper.student.StudentInfo
import io.github.wulkanowy.sdk.scrapper.student.StudentPhoto
import io.github.wulkanowy.sdk.scrapper.timetable.CacheResponse
import io.github.wulkanowy.sdk.scrapper.timetable.CompletedLesson
import io.github.wulkanowy.sdk.scrapper.timetable.CompletedLessonsRequest
import io.github.wulkanowy.sdk.scrapper.timetable.Timetable
import io.github.wulkanowy.sdk.scrapper.timetable.TimetableRequest
import io.github.wulkanowy.sdk.scrapper.timetable.mapCompletedLessonsList
import io.github.wulkanowy.sdk.scrapper.timetable.mapTimetableAdditional
import io.github.wulkanowy.sdk.scrapper.timetable.mapTimetableHeaders
import io.github.wulkanowy.sdk.scrapper.timetable.mapTimetableList
import io.github.wulkanowy.sdk.scrapper.toFormat
import org.jsoup.Jsoup
import java.net.HttpURLConnection.HTTP_NOT_FOUND
import java.time.LocalDate
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

internal class StudentRepository(
    private val api: StudentService,
    private val studentPlusService: StudentPlusService,
) {

    private var isEduOne: Boolean = false

    private fun LocalDate.toISOFormat(): String = toFormat("yyyy-MM-dd'T00:00:00'")

    private suspend fun getCache(): CacheResponse {
        if (isEduOne) error("Cache unavailable in eduOne compatibility mode")
        val startPage = getStartPage()

        isEduOne = getScriptFlag("isEduOne", startPage)
        if (isEduOne) error("Unsupported eduOne detected!")

        val res = api.getUserCache(
            token = getScriptParam("antiForgeryToken", startPage),
            appGuid = getScriptParam("appGuid", startPage),
            appVersion = getScriptParam("version", startPage),
        ).handleErrors()

        val data = requireNotNull(res.data) {
            "Required value was null. $res"
        }
        return data
    }

    suspend fun authorizePermission(pesel: String): Boolean {
        val startPage = getStartPage()
        return api.authorizePermission(
            token = getScriptParam("antiForgeryToken", startPage),
            appGuid = getScriptParam("appGuid", startPage),
            appVersion = getScriptParam("version", startPage),
            body = AuthorizePermissionRequest(AuthorizePermission(pesel)),
        ).handleErrors().data?.success ?: false
    }

    suspend fun getStudent(studentId: Int, unitId: Int): RegisterStudent? {
        return getStudentsFromDiaries(
            cache = getCache(),
            diaries = api.getDiaries().handleErrors().data.orEmpty(),
            unitId = unitId,
        ).find {
            it.studentId == studentId
        }
    }

    @Suppress("UnnecessaryOptInAnnotation")
    @OptIn(ExperimentalEncodingApi::class)
    suspend fun getAttendance(startDate: LocalDate, endDate: LocalDate?, studentId: Int, diaryId: Int): List<Attendance> {
        val lessonTimes = runCatching { getCache().times }
        if (lessonTimes.isFailure && isEduOne) {
            return studentPlusService.getAttendance(
                key = Base64.encode("$studentId-$diaryId-1".toByteArray()),
                from = startDate.toISOFormat(),
                to = endDate?.toISOFormat() ?: startDate.plusDays(7).toISOFormat(),
            )
        }
        return api.getAttendance(AttendanceRequest(startDate.atStartOfDay()))
            .handleErrors()
            .data?.mapAttendanceList(startDate, endDate, lessonTimes.getOrThrow()).orEmpty()
    }

    suspend fun getAttendanceSummary(subjectId: Int?): List<AttendanceSummary> {
        return api.getAttendanceStatistics(AttendanceSummaryRequest(subjectId))
            .handleErrors()
            .data?.mapAttendanceSummaryList().orEmpty()
    }

    suspend fun excuseForAbsence(absents: List<Absent>, content: String?): Boolean {
        val startPage = getStartPage()
        return api.excuseForAbsence(
            token = getScriptParam("antiForgeryToken", startPage),
            appGuid = getScriptParam("appGuid", startPage),
            appVersion = getScriptParam("version", startPage),
            attendanceExcuseRequest = AttendanceExcuseRequest(
                AttendanceExcuseRequest.Excuse(
                    absents = absents.map { absence ->
                        AttendanceExcuseRequest.Excuse.Absent(
                            date = absence.date.toFormat("yyyy-MM-dd'T'HH:mm:ss"),
                            timeId = absence.timeId,
                        )
                    },
                    content = content,
                ),
            ),
        ).handleErrors().success
    }

    suspend fun getSubjects(): List<Subject> {
        return api.getAttendanceSubjects().handleErrors().data.orEmpty()
    }

    suspend fun getExams(startDate: LocalDate, endDate: LocalDate? = null): List<Exam> {
        return api.getExams(ExamRequest(startDate.atStartOfDay(), startDate.getSchoolYear()))
            .handleErrors()
            .data.orEmpty().mapExamsList(startDate, endDate)
    }

    suspend fun getGrades(semesterId: Int): Grades {
        val data = api.getGrades(GradeRequest(semesterId)).handleErrors().data

        return Grades(
            details = data?.mapGradesList().orEmpty(),
            summary = data?.mapGradesSummary().orEmpty(),
            descriptive = data?.gradesDescriptive.orEmpty(),
            isAverage = data?.isAverage ?: false,
            isPoints = data?.isPoints ?: false,
            isForAdults = data?.isForAdults ?: false,
            type = data?.type ?: -1,
        )
    }

    suspend fun getGradesPartialStatistics(semesterId: Int): List<GradesStatisticsPartial> {
        return api.getGradesPartialStatistics(GradesStatisticsRequest(semesterId))
            .handleErrors()
            .data.orEmpty().mapGradesStatisticsPartial()
    }

    suspend fun getGradesPointsStatistics(semesterId: Int): List<GradePointsSummary> {
        return api.getGradesPointsStatistics(GradesStatisticsRequest(semesterId))
            .handleErrors()
            .data?.items.orEmpty()
    }

    suspend fun getGradesAnnualStatistics(semesterId: Int): List<GradesStatisticsSemester> {
        return api.getGradesAnnualStatistics(GradesStatisticsRequest(semesterId))
            .handleErrors()
            .data.orEmpty().mapGradesStatisticsSemester()
    }

    suspend fun getHomework(startDate: LocalDate, endDate: LocalDate? = null): List<Homework> {
        return api.getHomework(HomeworkRequest(startDate.atStartOfDay(), startDate.getSchoolYear(), -1))
            .handleErrors()
            .data.orEmpty().mapHomework(startDate, endDate)
    }

    suspend fun getNotes(): List<Note> {
        return api.getNotes().handleErrors().data?.notes.orEmpty().map {
            it.copy(
                teacher = it.teacher.split(" [").first(),
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

    suspend fun getMenu(date: LocalDate): List<Menu> {
        val menuRequest = MenuRequest(date = date.atStartOfDay())
        return api.getMenu(menuRequest).handleErrors().data.orEmpty()
    }

    suspend fun getTimetable(startDate: LocalDate, endDate: LocalDate? = null): Timetable {
        val data = api.getTimetable(TimetableRequest(startDate.toISOFormat())).handleErrors().data

        return Timetable(
            headers = data?.mapTimetableHeaders().orEmpty(),
            lessons = data?.mapTimetableList(startDate, endDate).orEmpty(),
            additional = data?.mapTimetableAdditional().orEmpty(),
        )
    }

    suspend fun getCompletedLessons(start: LocalDate, endDate: LocalDate?, subjectId: Int): List<CompletedLesson> {
        val end = endDate ?: start.plusMonths(1)
        val cache = getCache()
        if (!cache.showCompletedLessons) throw FeatureDisabledException("Widok lekcji zrealizowanych został wyłączony przez Administratora szkoły")

        val res = api.getCompletedLessons(CompletedLessonsRequest(start.toISOFormat(), end.toISOFormat(), subjectId))
        return res.handleErrors().mapCompletedLessonsList(start, endDate)
    }

    suspend fun getTeachers(): List<Teacher> {
        return api.getSchoolAndTeachers().handleErrors().data?.mapToTeachers().orEmpty()
    }

    suspend fun getSchool(): School {
        return api.getSchoolAndTeachers().handleErrors().let {
            requireNotNull(it.data) { "Required value was null. $it" }
        }.mapToSchool()
    }

    suspend fun getStudentInfo(): StudentInfo {
        return api.getStudentInfo().handleErrors().let {
            requireNotNull(it.data) {
                "Required value was null. $it"
            }
        }
    }

    suspend fun getStudentPhoto(): StudentPhoto {
        return api.getStudentPhoto().handleErrors().let {
            requireNotNull(it.data) {
                "Required value was null. $it"
            }
        }
    }

    suspend fun getRegisteredDevices(): List<Device> {
        return api.getRegisteredDevices().handleErrors().data.orEmpty()
    }

    suspend fun getToken(): TokenResponse {
        val res = api.getToken().handleErrors()
        return requireNotNull(res.data) {
            "Required value was null. $res"
        }.copy(
            qrCodeImage = Jsoup.parse(res.data.qrCodeImage)
                .select("img")
                .attr("src")
                .split("data:image/png;base64,")[1],
        )
    }

    suspend fun unregisterDevice(id: Int): Boolean {
        val it = getStartPage()
        return api.unregisterDevice(
            getScriptParam("antiForgeryToken", it),
            getScriptParam("appGuid", it),
            getScriptParam("version", it),
            UnregisterDeviceRequest(id),
        ).handleErrors().success
    }

    private suspend fun getStartPage(): String {
        return runCatching {
            api.getStart("App")
        }.recoverCatching {
            if (it is ScrapperException && it.code == HTTP_NOT_FOUND) {
                api.getStart("Start")
            } else throw it
        }.getOrThrow()
    }
}

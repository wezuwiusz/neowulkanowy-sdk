package io.github.wulkanowy.sdk.scrapper.repository

import io.github.wulkanowy.sdk.scrapper.attendance.Absent
import io.github.wulkanowy.sdk.scrapper.attendance.Attendance
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceCategory
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceExcusePlusRequest
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceExcusePlusRequestItem
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceExcusePlusResponseItem
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceExcusesPlusResponse
import io.github.wulkanowy.sdk.scrapper.attendance.SentExcuseStatus
import io.github.wulkanowy.sdk.scrapper.exams.Exam
import io.github.wulkanowy.sdk.scrapper.exception.FeatureDisabledException
import io.github.wulkanowy.sdk.scrapper.exception.VulcanClientError
import io.github.wulkanowy.sdk.scrapper.getEncodedKey
import io.github.wulkanowy.sdk.scrapper.grades.Grades
import io.github.wulkanowy.sdk.scrapper.grades.mapGradesList
import io.github.wulkanowy.sdk.scrapper.grades.mapGradesSummary
import io.github.wulkanowy.sdk.scrapper.handleErrors
import io.github.wulkanowy.sdk.scrapper.homework.Homework
import io.github.wulkanowy.sdk.scrapper.mobile.TokenResponse
import io.github.wulkanowy.sdk.scrapper.register.AuthorizePermissionPlusRequest
import io.github.wulkanowy.sdk.scrapper.register.RegisterStudent
import io.github.wulkanowy.sdk.scrapper.service.StudentPlusService
import io.github.wulkanowy.sdk.scrapper.timetable.CompletedLesson
import io.github.wulkanowy.sdk.scrapper.timetable.mapCompletedLessons
import io.github.wulkanowy.sdk.scrapper.toFormat
import org.jsoup.Jsoup
import java.net.HttpURLConnection
import java.time.LocalDate

internal class StudentPlusRepository(
    private val api: StudentPlusService,
) {

    private fun LocalDate.toISOFormat(): String = toFormat("yyyy-MM-dd'T00:00:00'")

    suspend fun authorizePermission(pesel: String, studentId: Int, diaryId: Int, unitId: Int): Boolean {
        runCatching {
            api.authorize(
                AuthorizePermissionPlusRequest(
                    key = getEncodedKey(studentId, diaryId, unitId),
                    pesel = pesel,
                ),
            )
        }.onFailure {
            if (it is VulcanClientError && it.httpCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                if ("odrzucona" in it.message.orEmpty()) {
                    return false
                }
            }
        }.getOrThrow()
        return true
    }

    suspend fun getStudent(studentId: Int, diaryId: Int, unitId: Int): RegisterStudent? {
        return api.getContext().students.find {
            it.key == getEncodedKey(studentId, diaryId, unitId)
        }?.let {
            RegisterStudent(
                studentId = studentId,
                studentName = it.studentName.substringBefore(" "),
                studentSecondName = "", //
                studentSurname = it.studentName.substringAfterLast(" "),
                className = it.className,
                classId = 0, //
                isParent = it.opiekunUcznia,
                semesters = listOf(), //
                isAuthorized = !it.isAuthorizationRequired,
            )
        }
    }

    suspend fun getAttendance(startDate: LocalDate, endDate: LocalDate?, studentId: Int, diaryId: Int, unitId: Int): List<Attendance> {
        val key = getEncodedKey(studentId, diaryId, unitId)
        val from = startDate.toISOFormat()
        val to = endDate?.toISOFormat() ?: startDate.plusDays(7).toISOFormat()

        val attendanceItems = api.getAttendance(key = key, from = from, to = to)
        val sentExcuses = api.getExcuses(key = key, from = from, to = to)

        return attendanceItems.onEach {
            it.category = AttendanceCategory.getCategoryById(it.categoryId)

            val sentExcuse = it.getMatchingExcuse(sentExcuses)

            it.excusable = it.isExcusable(sentExcuses.isExcusesActive, sentExcuse)
            if (sentExcuse != null) it.excuseStatus = SentExcuseStatus.getByValue(sentExcuse.status)
        }
    }

    private fun Attendance.getMatchingExcuse(sentExcuses: AttendanceExcusesPlusResponse): AttendanceExcusePlusResponseItem? {
        return sentExcuses.excuses.find { excuse ->
            excuse.dayDate == date && (excuse.lessonNumber == number || excuse.lessonNumber == null)
        }
    }

    private fun Attendance.isExcusable(isExcusesActive: Boolean, sentExcuse: AttendanceExcusePlusResponseItem?): Boolean {
        val isUnexcused = category == AttendanceCategory.ABSENCE_UNEXCUSED
        val isUnexcusedLateness = category == AttendanceCategory.UNEXCUSED_LATENESS
        val isStatusMatch = isUnexcused || isUnexcusedLateness

        return isStatusMatch && isExcusesActive && sentExcuse == null
    }

    suspend fun excuseForAbsence(absents: List<Absent>, content: String?, studentId: Int, diaryId: Int, unitId: Int): Boolean {
        api.excuseForAbsence(
            body = AttendanceExcusePlusRequest(
                key = getEncodedKey(studentId, diaryId, unitId),
                content = content.orEmpty(),
                excuses = absents.map {
                    AttendanceExcusePlusRequestItem(
                        date = it.date.toFormat("yyyy-MM-dd'T'HH:mm:ss"),
                        lessonHourId = it.timeId,
                    )
                },
            ),
        ).handleErrors()

        return true
    }

    suspend fun getCompletedLessons(startDate: LocalDate, endDate: LocalDate?, studentId: Int, diaryId: Int, unitId: Int): List<CompletedLesson> {
        val key = getEncodedKey(studentId, diaryId, unitId)
        val context = api.getContext()
        val studentConfig = context.students.find { it.key == key }?.config

        if (studentConfig?.showCompletedLessons != true) {
            throw FeatureDisabledException("Widok lekcji zrealizowanych został wyłączony przez Administratora szkoły")
        }

        return api.getCompletedLessons(
            key = key,
            status = 1,
            from = startDate.toISOFormat(),
            to = endDate?.toISOFormat() ?: startDate.plusDays(7).toISOFormat(),
        ).mapCompletedLessons(startDate, endDate)
    }

    suspend fun getToken(): TokenResponse {
        val res = api.getDeviceRegistrationToken()
        return res.copy(
            qrCodeImage = Jsoup.parse(res.qrCodeImage)
                .select("img")
                .attr("src")
                .split("data:image/png;base64,")[1],
        )
    }

    suspend fun getGrades(semesterId: Int, studentId: Int, diaryId: Int, unitId: Int): Grades {
        val key = getEncodedKey(studentId, diaryId, unitId)
        val res = api.getGrades(key, semesterId)

        return Grades(
            details = res.mapGradesList(),
            summary = res.mapGradesSummary(),
            descriptive = res.gradesDescriptive,
            isAverage = res.isAverage,
            isPoints = res.isPoints,
            isForAdults = res.isForAdults,
            type = res.type,
        )
    }

    suspend fun getExams(startDate: LocalDate, endDate: LocalDate?, studentId: Int, diaryId: Int, unitId: Int): List<Exam> {
        val key = getEncodedKey(studentId, diaryId, unitId)
        val examsHomeworkRes = api.getExamsAndHomework(
            key = key,
            from = startDate.toISOFormat(),
            to = endDate?.toISOFormat(),
        )

        return examsHomeworkRes.filter { it.type != 4 }.map { exam ->
            val examDetailsRes = api.getExamDetails(key, exam.id)
            Exam(
                entryDate = exam.date,
                subject = exam.subject,
                type = exam.type,
                description = examDetailsRes.description,
                teacher = examDetailsRes.teacher,
            ).apply {
                typeName = when (exam.type) {
                    1 -> "Sprawdzian"
                    2 -> "Kartkówka"
                    else -> "Praca klasowa"
                }
                date = exam.date
                teacherSymbol = null
            }
        }
    }

    suspend fun getHomework(startDate: LocalDate, endDate: LocalDate?, studentId: Int, diaryId: Int, unitId: Int): List<Homework> {
        val key = getEncodedKey(studentId, diaryId, unitId)
        val examsHomeworkRes = api.getExamsAndHomework(
            key = key,
            from = startDate.toISOFormat(),
            to = endDate?.toISOFormat(),
        )

        return examsHomeworkRes.filter { it.type == 4 }.map { homework ->
            val homeworkDetailsRes = api.getHomeworkDetails(key, homework.id)
            return Homework(
                homeworkId = homework.id,
                subject = homework.subject,
                teacher = homeworkDetailsRes.teacher,
                description = homeworkDetailsRes.description,
                date = homework.date,
                status = homeworkDetailsRes.status,
                isAnswerRequired = homeworkDetailsRes.isAnswerRequired,
            ).apply {
                teacherSymbol = null
            }
        }
    }
}

package io.github.wulkanowy.sdk.scrapper.repository

import io.github.wulkanowy.sdk.scrapper.attendance.Attendance
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceCategory
import io.github.wulkanowy.sdk.scrapper.exception.FeatureDisabledException
import io.github.wulkanowy.sdk.scrapper.exception.VulcanClientError
import io.github.wulkanowy.sdk.scrapper.handleErrors
import io.github.wulkanowy.sdk.scrapper.register.AuthorizePermissionPlusRequest
import io.github.wulkanowy.sdk.scrapper.register.RegisterStudent
import io.github.wulkanowy.sdk.scrapper.service.StudentPlusService
import io.github.wulkanowy.sdk.scrapper.timetable.CompletedLesson
import io.github.wulkanowy.sdk.scrapper.timetable.mapCompletedLessons
import io.github.wulkanowy.sdk.scrapper.toFormat
import java.net.HttpURLConnection
import java.time.LocalDate
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

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
        return api.getAttendance(
            key = getEncodedKey(studentId, diaryId, unitId),
            from = startDate.toISOFormat(),
            to = endDate?.toISOFormat() ?: startDate.plusDays(7).toISOFormat(),
        ).onEach {
            it.category = AttendanceCategory.getCategoryById(it.categoryId)
        }
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

    @OptIn(ExperimentalEncodingApi::class)
    private fun getEncodedKey(studentId: Int, diaryId: Int, unitId: Int): String {
        return Base64.encode("$studentId-$diaryId-1-$unitId".toByteArray())
    }
}

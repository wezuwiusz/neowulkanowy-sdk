package io.github.wulkanowy.sdk.scrapper.repository

import io.github.wulkanowy.sdk.scrapper.attendance.Attendance
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceCategory
import io.github.wulkanowy.sdk.scrapper.service.StudentPlusService
import io.github.wulkanowy.sdk.scrapper.timetable.CompletedLesson
import io.github.wulkanowy.sdk.scrapper.timetable.mapCompletedLessons
import io.github.wulkanowy.sdk.scrapper.toFormat
import java.time.LocalDate
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

internal class StudentPlusRepository(
    private val api: StudentPlusService,
) {

    private fun LocalDate.toISOFormat(): String = toFormat("yyyy-MM-dd'T00:00:00'")

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
        return api.getCompletedLessons(
            key = getEncodedKey(studentId, diaryId, unitId),
            from = startDate.toISOFormat(),
            to = endDate?.toISOFormat() ?: startDate.plusDays(7).toISOFormat(),
        ).mapCompletedLessons(startDate, endDate)
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun getEncodedKey(studentId: Int, diaryId: Int, unitId: Int): String {
        return Base64.encode("$studentId-$diaryId-1-$unitId".toByteArray())
    }
}

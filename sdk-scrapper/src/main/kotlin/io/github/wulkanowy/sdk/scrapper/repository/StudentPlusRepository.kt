package io.github.wulkanowy.sdk.scrapper.repository

import io.github.wulkanowy.sdk.scrapper.attendance.Attendance
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceCategory
import io.github.wulkanowy.sdk.scrapper.exception.FeatureDisabledException
import io.github.wulkanowy.sdk.scrapper.service.StudentPlusService
import io.github.wulkanowy.sdk.scrapper.timetable.CacheEduOneResponse
import io.github.wulkanowy.sdk.scrapper.timetable.CompletedLesson
import io.github.wulkanowy.sdk.scrapper.toFormat
import java.time.LocalDate
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

internal class StudentPlusRepository(
    private val api: StudentPlusService,
) {

    private fun LocalDate.toISOFormat(): String = toFormat("yyyy-MM-dd'T00:00:00'")

    private suspend fun getCache(): CacheEduOneResponse {
        return api.getUserCache()
    }

    suspend fun getAttendance(startDate: LocalDate, endDate: LocalDate?, studentId: Int, diaryId: Int): List<Attendance> {
        return api.getAttendance(
            key = getEncodedKey(studentId, diaryId),
            from = startDate.toISOFormat(),
            to = endDate?.toISOFormat() ?: startDate.plusDays(7).toISOFormat(),
        ).onEach {
            it.category = AttendanceCategory.getCategoryById(it.categoryId)
        }
    }

    suspend fun getCompletedLessons(): List<CompletedLesson> {
        val cache = getCache()
        if (!cache.showCompletedLessons) throw FeatureDisabledException("Widok lekcji zrealizowanych został wyłączony przez Administratora szkoły")
        TODO("Not yet implemented")
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun getEncodedKey(studentId: Int, diaryId: Int): String {
        return Base64.encode("$studentId-$diaryId-1".toByteArray())
    }
}

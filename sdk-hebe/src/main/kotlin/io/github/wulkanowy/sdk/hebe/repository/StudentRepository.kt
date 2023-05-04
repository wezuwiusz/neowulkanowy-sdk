package io.github.wulkanowy.sdk.hebe.repository

import io.github.wulkanowy.sdk.hebe.getEnvelopeOrThrowError
import io.github.wulkanowy.sdk.hebe.models.Exam
import io.github.wulkanowy.sdk.hebe.models.Grade
import io.github.wulkanowy.sdk.hebe.models.GradeAverage
import io.github.wulkanowy.sdk.hebe.models.GradeSummary
import io.github.wulkanowy.sdk.hebe.service.StudentService
import java.time.LocalDate
import java.time.format.DateTimeFormatter

internal class StudentRepository(private val studentService: StudentService) {

    suspend fun getGrades(pupilId: Int, periodId: Int): List<Grade> {
        return studentService.getGrades(
            createQueryMap(pupilId = pupilId, periodId = periodId),
        ).getEnvelopeOrThrowError().orEmpty()
    }

    suspend fun getGradesSummary(pupilId: Int, periodId: Int): List<GradeSummary> {
        return studentService.getGradesSummary(
            createQueryMap(pupilId = pupilId, periodId = periodId),
        ).getEnvelopeOrThrowError().orEmpty()
    }

    suspend fun getGradesAverage(pupilId: Int, periodId: Int): List<GradeAverage> {
        return studentService.getGradesAverage(
            createQueryMap(pupilId = pupilId, periodId = periodId),
        ).getEnvelopeOrThrowError().orEmpty()
    }

    suspend fun getExams(pupilId: Int, startDate: LocalDate, endDate: LocalDate): List<Exam> {
        return studentService.getExams(
            createQueryMap(pupilId = pupilId, dateFrom = startDate),
        ).getEnvelopeOrThrowError().orEmpty().filter {
            it.deadline.date in startDate..endDate
        }
    }

    private fun createQueryMap(
        pupilId: Int,
        periodId: Int? = null,
        dateFrom: LocalDate? = null,
        dateTo: LocalDate? = null,
    ): Map<String, Any?> = mapOf(
        "pupilId" to pupilId,
        "periodId" to periodId,
        "lastSyncDate" to "1970-01-01 01:00:00",
        "lastId" to Int.MIN_VALUE,
        "pageSize" to 500,
        "dateFrom" to dateFrom?.format(DateTimeFormatter.ISO_DATE),
        "dateTo" to dateTo?.format(DateTimeFormatter.ISO_DATE),
    ).filterValues { it != null }
}

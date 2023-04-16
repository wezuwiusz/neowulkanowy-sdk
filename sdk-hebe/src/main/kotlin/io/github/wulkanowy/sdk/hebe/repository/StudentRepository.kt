package io.github.wulkanowy.sdk.hebe.repository

import io.github.wulkanowy.sdk.hebe.models.Grade
import io.github.wulkanowy.sdk.hebe.service.StudentService

internal class StudentRepository(private val studentService: StudentService) {

    suspend fun getGrades(pupilId: Int, periodId: Int): List<Grade>? {
        return studentService.getGrades(
            pupilId = pupilId,
            periodId = periodId,
        ).envelope
    }
}

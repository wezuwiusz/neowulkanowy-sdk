package io.github.wulkanowy.sdk.scrapper.repository

import io.github.wulkanowy.sdk.scrapper.interceptor.handleErrors
import io.github.wulkanowy.sdk.scrapper.register.Semester
import io.github.wulkanowy.sdk.scrapper.register.toSemesters
import io.github.wulkanowy.sdk.scrapper.service.StudentService
import org.slf4j.LoggerFactory

class StudentStartRepository(
    private val studentId: Int,
    private val classId: Int,
    private val unitId: Int,
    private val api: StudentService,
) {

    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    suspend fun getSemesters(): List<Semester> {
        val diaries = api.getDiaries().handleErrors().data
        return diaries?.toSemesters(studentId, classId, unitId).orEmpty()
            .sortedByDescending { it.semesterId }
            .ifEmpty {
                logger.debug("Student $studentId, class $classId not found in diaries: $diaries")
                emptyList()
            }
    }
}

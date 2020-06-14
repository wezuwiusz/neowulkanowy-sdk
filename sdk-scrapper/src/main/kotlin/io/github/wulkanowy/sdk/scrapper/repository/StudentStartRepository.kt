package io.github.wulkanowy.sdk.scrapper.repository

import io.github.wulkanowy.sdk.scrapper.interceptor.handleErrors
import io.github.wulkanowy.sdk.scrapper.register.Semester
import io.github.wulkanowy.sdk.scrapper.service.StudentService
import io.github.wulkanowy.sdk.scrapper.toLocalDate
import org.slf4j.LoggerFactory

class StudentStartRepository(
    private val studentId: Int,
    private val classId: Int,
    private val api: StudentService
) {

    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    suspend fun getSemesters(): List<Semester> {
        val diaries = api.getDiaries().handleErrors().data
        return diaries.orEmpty()
            .asSequence()
            .filter { it.semesters?.isNotEmpty() ?: false }
            .filter { it.studentId == studentId }
            .filter { it.semesters!![0].classId == classId }
            .map { diary ->
                diary.semesters!!.map {
                    Semester(
                        diaryId = diary.diaryId,
                        diaryName = "${diary.level}${diary.symbol}",
                        schoolYear = diary.year,
                        semesterId = it.id,
                        semesterNumber = it.number,
                        start = it.start.toLocalDate(),
                        end = it.end.toLocalDate(),
                        classId = it.classId,
                        unitId = it.unitId
                    )
                }
            }
            .flatten()
            .sortedByDescending { it.semesterId }
            .toList()
            .ifEmpty {
                logger.debug("Student $studentId, class $classId not found in diaries: $diaries")
                emptyList()
            }
    }
}

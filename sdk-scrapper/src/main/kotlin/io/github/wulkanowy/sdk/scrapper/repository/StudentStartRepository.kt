package io.github.wulkanowy.sdk.scrapper.repository

import io.github.wulkanowy.sdk.scrapper.interceptor.ErrorHandlerTransformer
import io.github.wulkanowy.sdk.scrapper.register.Semester
import io.github.wulkanowy.sdk.scrapper.service.StudentService
import io.github.wulkanowy.sdk.scrapper.toLocalDate
import io.reactivex.Single
import org.slf4j.LoggerFactory
import org.threeten.bp.LocalDate.now

class StudentStartRepository(
    private val studentId: Int,
    private val classId: Int,
    private val api: StudentService
) {

    companion object {
        @JvmStatic private val logger = LoggerFactory.getLogger(this::class.java)
    }

    fun getSemesters(): Single<List<Semester>> {
        return api.getDiaries()
            .compose(ErrorHandlerTransformer())
            .map { it.data.orEmpty() }
            .map { diaries ->
                diaries.asSequence()
                    .filter { diary -> diary.semesters?.isNotEmpty() ?: false }
                    .filter { diary -> diary.studentId == studentId }
                    .filter { diary -> diary.semesters!![0].classId == classId }
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
                                current = it.start.toLocalDate() <= now() && it.end.toLocalDate() >= now(),
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
            }.map {
                if (it.isNotEmpty() && it.singleOrNull { semester -> semester.current } == null) it.apply { first().current = true }
                else it
            }
    }
}

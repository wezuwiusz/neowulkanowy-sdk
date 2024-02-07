package io.github.wulkanowy.sdk.scrapper.register

import io.github.wulkanowy.sdk.scrapper.repository.RegisterRepository
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(RegisterRepository::class.java)

internal fun List<Diary>.toSemesters(studentId: Int, classId: Int, unitId: Int): List<Semester> = this
    .filter { it.studentId == studentId }
    .filter { (it.semesters?.firstOrNull()?.classId ?: 0) == classId }
    .flatMap { diary ->
        when {
            diary.kindergartenDiaryId != 0 -> listOf(
                Semester(
                    diaryId = diary.diaryId,
                    kindergartenDiaryId = diary.kindergartenDiaryId,
                    diaryName = "${diary.level}${diary.symbol}",
                    schoolYear = diary.year,
                    semesterId = 0,
                    semesterNumber = 1,
                    start = diary.start.toLocalDate(),
                    end = diary.end.toLocalDate(),
                    classId = classId,
                    className = diary.symbol,
                    unitId = unitId,
                ),
            )
            !diary.semesters.isNullOrEmpty() -> diary.semesters.map {
                Semester(
                    diaryId = diary.diaryId,
                    kindergartenDiaryId = 0,
                    diaryName = "${diary.level}${diary.symbol}",
                    schoolYear = diary.year,
                    semesterId = it.id,
                    semesterNumber = it.number,
                    start = it.start.toLocalDate(),
                    end = it.end.toLocalDate(),
                    classId = it.classId,
                    className = diary.symbol,
                    unitId = it.unitId,
                )
            }
            else -> {
                logger.error("No supported student found in diaries: $this")
                emptyList()
            }
        }
    }

package io.github.wulkanowy.sdk.scrapper.register

import io.github.wulkanowy.sdk.scrapper.toLocalDate

fun List<Diary>.toSemesters(studentId: Int, classId: Int, unitId: Int): List<Semester> = this
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
                    unitId = unitId,
                )
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
                    unitId = it.unitId
                )
            }
            else -> throw IllegalArgumentException("Unknown diary format: $diary")
        }
    }

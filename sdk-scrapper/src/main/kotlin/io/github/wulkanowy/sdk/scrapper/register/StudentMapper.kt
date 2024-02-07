package io.github.wulkanowy.sdk.scrapper.register

import io.github.wulkanowy.sdk.scrapper.timetable.CacheResponse

internal fun getStudentsFromDiaries(
    diaries: List<Diary>,
    cache: CacheResponse?,
    unitId: Int,
): List<RegisterStudent> = diaries
    .filter { it.semesters.orEmpty().isNotEmpty() || it.kindergartenDiaryId != 0 }
    .sortedByDescending { it.level }
    .distinctBy { listOf(it.studentId, it.semesters?.firstOrNull()?.classId ?: it.symbol) }
    .map { diary ->
        val classId = diary.semesters?.firstOrNull()?.classId ?: 0
        RegisterStudent(
            studentId = diary.studentId,
            studentName = diary.studentName.trim(),
            studentSecondName = diary.studentSecondName.orEmpty(),
            studentSurname = diary.studentSurname,
            className = diary.symbol.orEmpty(),
            classId = classId,
            isParent = cache?.isParent == true,
            isAuthorized = diary.isAuthorized == true,
            semesters = diaries.toSemesters(
                studentId = diary.studentId,
                classId = classId,
                unitId = unitId,
            ),
        )
    }

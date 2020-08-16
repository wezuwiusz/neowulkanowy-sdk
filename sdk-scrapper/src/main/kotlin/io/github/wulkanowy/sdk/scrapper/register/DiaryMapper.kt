package io.github.wulkanowy.sdk.scrapper.register

import io.github.wulkanowy.sdk.scrapper.toLocalDate

fun Diary.toSemesters() = semesters!!.map {
    Semester(
        diaryId = diaryId,
        diaryName = "$level$symbol",
        schoolYear = year,
        semesterId = it.id,
        semesterNumber = it.number,
        start = it.start.toLocalDate(),
        end = it.end.toLocalDate(),
        classId = it.classId,
        unitId = it.unitId
    )
}

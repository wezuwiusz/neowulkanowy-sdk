package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.Semester
import io.github.wulkanowy.sdk.mobile.register.Student
import io.github.wulkanowy.sdk.toLocalDate
import org.threeten.bp.LocalDate.now
import org.threeten.bp.LocalDate.of
import org.threeten.bp.Month
import io.github.wulkanowy.sdk.scrapper.register.Semester as ScrapperSemester

@JvmName("mapScrapperSemesters")
fun List<ScrapperSemester>.mapSemesters(): List<Semester> {
    return map {
        Semester(
            diaryId = it.diaryId,
            diaryName = it.diaryName,
            schoolYear = it.schoolYear,
            semesterId = it.semesterId,
            semesterNumber = it.semesterNumber,
            current = it.current,
            start = it.start,
            end = it.end,
            classId = it.classId,
            unitId = it.unitId
        )
    }
}

fun List<Student>.mapSemesters(studentId: Int): List<Semester> {
    return filter { it.id == studentId }.map {
        Semester(
            diaryId = 0,
            diaryName = it.classSymbol,
            schoolYear = it.periodDateFrom.toLocalDate().let { start -> if (start.month == Month.SEPTEMBER) start.year else start.year - 1 },
            semesterId = it.classificationPeriodId,
            semesterNumber = it.periodNumber,
            current = now() in it.periodDateFrom.toLocalDate()..it.periodDateTo.toLocalDate(),
            start = it.periodDateFrom.toLocalDate(),
            end = it.periodDateTo.toLocalDate(),
            classId = it.classId,
            unitId = it.reportingUnitId
        )
    }.let {
        if (it.size == 1) {
            val semesterNumber = it.single().semesterNumber
            listOf(it.single(), it.single().copy(
                current = false,
                semesterNumber = if (semesterNumber == 1) 2 else 1,
                semesterId = if (semesterNumber == 1) it.single().semesterId + 1 else it.single().semesterId - 1,
                start = if (semesterNumber == 1) it.single().end.plusDays(1) else of(it.single().schoolYear, 9, 1),
                end = if (semesterNumber == 1) of(it.single().schoolYear + 1, 6, 30) else it.single().start.plusDays(1)
            ))
        } else it
    }
}

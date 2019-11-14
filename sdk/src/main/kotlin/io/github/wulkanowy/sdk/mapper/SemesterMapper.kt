package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.Semester
import io.github.wulkanowy.sdk.mobile.register.Student
import io.github.wulkanowy.sdk.toLocalDate
import org.threeten.bp.LocalDate.now
import org.threeten.bp.Month
import io.github.wulkanowy.api.register.Semester as ScrapperSemester

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
            diaryId = -1,
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
    }
}

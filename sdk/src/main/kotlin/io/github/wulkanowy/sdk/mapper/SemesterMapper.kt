package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.exception.VulcanException
import io.github.wulkanowy.sdk.mobile.register.Student
import io.github.wulkanowy.sdk.pojo.Semester
import io.github.wulkanowy.sdk.toLocalDate
import java.time.LocalDate.of
import java.time.Month
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
            start = it.periodDateFrom.toLocalDate(),
            end = it.periodDateTo.toLocalDate(),
            classId = it.classId,
            unitId = it.reportingUnitId
        )
    }.mockSecondSemester()
}

private fun List<Semester>.mockSecondSemester(): List<Semester> {
    if (size != 1) throw VulcanException("Expected semester list size 1, get $size")
    val semester = single()
    return (this + semester.copy(
        semesterNumber = if (semester.semesterNumber == 1) 2 else 1,
        semesterId = if (semester.semesterNumber == 1) semester.semesterId + 1 else semester.semesterId - 1,
        start = if (semester.semesterNumber == 1) semester.end.plusDays(1) else of(semester.schoolYear, 9, 1),
        end = if (semester.semesterNumber == 1) of(semester.schoolYear + 1, 8, 31) else semester.start.minusDays(1)
    ))
}

package io.github.wulkanowy.sdk.scrapper.register

import io.github.wulkanowy.sdk.scrapper.getDecodedKey
import io.github.wulkanowy.sdk.scrapper.grades.GradeSemester

internal fun List<GradeSemester>.mapToSemester(contextStudent: ContextStudent): List<Semester> {
    val key = getDecodedKey(contextStudent.key)
    return ifEmpty {
        listOf(
            GradeSemester(
                dataOd = contextStudent.registerDateFrom,
                dataDo = contextStudent.registerDateTo,
                id = -1, //
                numerOkresu = 0, //
            ),
        )
    }.map { semester ->
        Semester(
            diaryId = contextStudent.registerId,
            diaryName = contextStudent.className,
            schoolYear = contextStudent.registerDateFrom.year,
            semesterId = semester.id,
            semesterNumber = semester.numerOkresu,
            start = semester.dataOd.toLocalDate(),
            end = semester.dataDo.toLocalDate(),
            className = contextStudent.className,
            unitId = key.unitId,
            classId = 0, // not available
            kindergartenDiaryId = 0, // not available?
        )
    }
}

internal fun ContextStudent.mapToRegisterStudent(semesters: List<GradeSemester>): RegisterStudent {
    val key = getDecodedKey(key)
    return RegisterStudent(
        studentId = key.studentId,
        studentName = studentName.substringBefore(" "),
        studentSurname = studentName.substringAfterLast(" "),
        className = className,
        isParent = opiekunUcznia,
        unitId = key.unitId,
        schoolName = schoolName,
        schoolNameShort = null,
        semesters = semesters.mapToSemester(this),
        isAuthorized = !isAuthorizationRequired,
        isEduOne = true, // we already in eduOne context here
        studentSecondName = "", //
        classId = 0, //
    )
}

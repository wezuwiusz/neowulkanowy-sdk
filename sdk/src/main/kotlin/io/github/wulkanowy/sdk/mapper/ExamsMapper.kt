package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.mobile.dictionaries.Dictionaries
import io.github.wulkanowy.sdk.pojo.Exam
import io.github.wulkanowy.sdk.scrapper.toLocalDate
import io.github.wulkanowy.sdk.toLocalDate
import io.github.wulkanowy.sdk.mobile.exams.Exam as ApiExam
import io.github.wulkanowy.sdk.scrapper.exams.Exam as ScrapperExam

fun List<ApiExam>.mapExams(dict: Dictionaries) = map { exam ->
    Exam(
        date = exam.date.toLocalDate(),
        entryDate = exam.date.toLocalDate(),
        description = exam.description,
        group = exam.divideName.orEmpty(),
        teacher = dict.teachers.singleOrNull { it.id == exam.employeeId }?.run { "$name $surname" }.orEmpty(),
        subject = dict.subjects.singleOrNull { it.id == exam.subjectId }?.name.orEmpty(),
        teacherSymbol = dict.teachers.singleOrNull { it.id == exam.employeeId }?.code.orEmpty(),
        type = when (exam.typeNumber) {
            1 -> "Sprawdzian"
            2 -> "Kartkówka"
            3 -> "Praca klasowa"
            else -> "Nieznany"
        }
    )
}

fun List<ScrapperExam>.mapExams() = map {
    Exam(
        date = it.date.toLocalDate(),
        entryDate = it.entryDate.toLocalDate(),
        description = it.description,
        group = it.group,
        teacherSymbol = it.teacherSymbol,
        teacher = it.teacher,
        subject = it.subject,
        type = it.type
    )
}

package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.api.toLocalDate
import io.github.wulkanowy.sdk.mobile.dictionaries.Dictionaries
import io.github.wulkanowy.sdk.pojo.Exam
import io.github.wulkanowy.sdk.toLocalDate
import io.github.wulkanowy.api.exams.Exam as ScrapperExam
import io.github.wulkanowy.sdk.mobile.exams.Exam as ApiExam

fun List<ApiExam>.mapExams(dict: Dictionaries): List<Exam> {
    return map { exam ->
        Exam(
            date = exam.date.toLocalDate(),
            entryDate = exam.date.toLocalDate(),
            description = exam.description,
            group = exam.divideName.orEmpty(),
            teacher = dict.teachers.singleOrNull { it.id == exam.employeeId }?.run { "$name $surname" }.orEmpty(),
            subject = dict.subjects.singleOrNull { it.id == exam.subjectId }?.name.orEmpty(),
            teacherSymbol = dict.teachers.singleOrNull { it.id == exam.employeeId }?.code.orEmpty(),
            type = if (exam.type) "Sprawdzian" else "Kartkówka"
        )
    }
}

fun List<ScrapperExam>.mapExams(): List<Exam> {
    return map {
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
}

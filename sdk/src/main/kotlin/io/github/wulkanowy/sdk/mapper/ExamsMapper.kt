package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.Exam
import io.github.wulkanowy.sdk.scrapper.exams.Exam as ScrapperExam

fun List<ScrapperExam>.mapExams() = map {
    Exam(
        date = it.date.toLocalDate(),
        entryDate = it.entryDate.toLocalDate(),
        description = it.description,
        teacherSymbol = it.teacherSymbol,
        teacher = it.teacher,
        subject = it.subject,
        type = it.typeName,
    )
}

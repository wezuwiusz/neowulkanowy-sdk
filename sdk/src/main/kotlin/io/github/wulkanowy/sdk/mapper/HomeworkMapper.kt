package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.mobile.dictionaries.Dictionaries
import io.github.wulkanowy.sdk.pojo.Homework
import io.github.wulkanowy.sdk.pojo.HomeworkAttachment
import io.github.wulkanowy.sdk.scrapper.toLocalDate
import io.github.wulkanowy.sdk.toLocalDate
import io.github.wulkanowy.sdk.mobile.homework.Homework as ApiHomework
import io.github.wulkanowy.sdk.scrapper.homework.Homework as ScrapperHomework

fun List<ApiHomework>.mapHomework(dictionaries: Dictionaries) = map {
    val employee = dictionaries.employees.singleOrNull { employee -> employee.id == it.employeeId }
    Homework(
        date = it.date.toLocalDate(),
        entryDate = it.date.toLocalDate(),
        subject = dictionaries.subjects.singleOrNull { subject -> subject.id == it.subjectId }?.name.orEmpty(),
        content = it.content,
        teacherSymbol = employee?.code.orEmpty(),
        teacher = employee?.run { "$name $surname" }.orEmpty(),
        attachments = emptyList(),
    )
}

fun List<ScrapperHomework>.mapHomework() = map {
    Homework(
        date = it.date.toLocalDate(),
        teacher = it.teacher,
        teacherSymbol = it.teacherSymbol,
        content = it.content,
        subject = it.subject,
        entryDate = it.entryDate.toLocalDate(),
        attachments = it._attachments.map { (url, name) ->
            HomeworkAttachment(url, name)
        },
    )
}

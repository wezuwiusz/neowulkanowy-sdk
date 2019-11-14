package io.github.wulkanowy.sdk.homework

import io.github.wulkanowy.api.toLocalDate
import io.github.wulkanowy.sdk.dictionaries.Dictionaries
import io.github.wulkanowy.sdk.pojo.Homework
import io.github.wulkanowy.sdk.toLocalDate
import io.github.wulkanowy.api.homework.Homework as ScrapperHomework
import io.github.wulkanowy.sdk.homework.Homework as ApiHomework

fun List<ApiHomework>.mapHomework(dictionaries: Dictionaries): List<Homework> {
    return map {
        val employee = dictionaries.employees.singleOrNull { employee -> employee.id == it.employeeId }
        Homework(
            date = it.date.toLocalDate(),
            entryDate = it.date.toLocalDate(),
            subject = dictionaries.subjects.singleOrNull { subject -> subject.id == it.subjectId }?.name.orEmpty(),
            content = it.content,
            teacherSymbol = employee?.code.orEmpty(),
            teacher = employee?.run { "$name $surname" }.orEmpty()
        )
    }
}

fun List<ScrapperHomework>.mapHomework(): List<Homework> {
    return map {
        Homework(
            date = it.date.toLocalDate(),
            teacher = it.teacher,
            teacherSymbol = it.teacherSymbol,
            content = it.content,
            subject = it.subject,
            entryDate = it.entryDate.toLocalDate()
        )
    }
}

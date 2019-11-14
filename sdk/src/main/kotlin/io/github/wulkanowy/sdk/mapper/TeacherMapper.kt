package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.mobile.dictionaries.Dictionaries
import io.github.wulkanowy.sdk.pojo.Teacher
import io.github.wulkanowy.sdk.scrapper.school.Teacher as ScrapperTeacher
import io.github.wulkanowy.sdk.mobile.school.Teacher as ApiTeacher

fun List<ApiTeacher>.mapTeachers(dictionaries: Dictionaries): List<Teacher> {
    return map { teacher ->
        val item = dictionaries.employees.singleOrNull { it.id == teacher.employeeId }
        Teacher(
            name = "${item?.name} ${item?.surname}",
            short = "${item?.code}",
            subject = dictionaries.subjects.singleOrNull { it.id == teacher.subjectId }?.name ?: teacher.role
        )
    }
}

fun List<ScrapperTeacher>.mapTeachers(): List<Teacher> {
    return map {
        Teacher(
            name = it.name,
            short = it.short,
            subject = it.subject
        )
    }
}

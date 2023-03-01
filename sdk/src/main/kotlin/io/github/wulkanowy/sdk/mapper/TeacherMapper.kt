package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.mobile.dictionaries.Dictionaries
import io.github.wulkanowy.sdk.pojo.Teacher
import io.github.wulkanowy.sdk.mobile.school.Teacher as ApiTeacher
import io.github.wulkanowy.sdk.scrapper.school.Teacher as ScrapperTeacher

fun List<ApiTeacher>.mapTeachers(dictionaries: Dictionaries) = mapNotNull { teacher ->
    val item = dictionaries.employees.singleOrNull { it.id == teacher.employeeId }
    if (item?.name == null) return@mapNotNull null
    Teacher(
        name = "${item.name} ${item.surname}",
        short = item.code,
        subject = dictionaries.subjects.singleOrNull { it.id == teacher.subjectId }?.name ?: teacher.role,
    )
}

fun List<ScrapperTeacher>.mapTeachers() = map {
    Teacher(
        name = it.name,
        short = it.short,
        subject = it.subject,
    )
}

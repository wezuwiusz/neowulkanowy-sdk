package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.Teacher
import io.github.wulkanowy.sdk.scrapper.school.Teacher as ScrapperTeacher

fun List<ScrapperTeacher>.mapTeachers() = map {
    Teacher(
        name = it.name,
        short = it.short,
        subject = it.subject,
    )
}

package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.getInitialsFromDisplayName
import io.github.wulkanowy.sdk.pojo.Teacher
import io.github.wulkanowy.sdk.hebe.models.Teacher as HebeTeacher
import io.github.wulkanowy.sdk.scrapper.school.Teacher as ScrapperTeacher

@JvmName("mapScrapperTeachers")
internal fun List<ScrapperTeacher>.mapTeachers() = map {
    Teacher(
        name = it.name,
        short = it.short,
        subject = it.subject,
    )
}

@JvmName("mapHebeTeachers")
internal fun List<HebeTeacher>.mapTeachers() = map {
    Teacher(
        name = it.displayName,
        short = when(it.displayName) {
            "" -> ""
            else ->it.displayName.getInitialsFromDisplayName()
        },
        subject = it.description,
    )
}

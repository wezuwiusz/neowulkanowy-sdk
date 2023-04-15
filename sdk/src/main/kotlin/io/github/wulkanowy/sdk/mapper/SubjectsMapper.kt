package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.Subject
import io.github.wulkanowy.sdk.scrapper.attendance.Subject as ScrapperSubject

fun List<ScrapperSubject>.mapSubjects() = map {
    Subject(
        id = it.value,
        name = it.name,
    )
}

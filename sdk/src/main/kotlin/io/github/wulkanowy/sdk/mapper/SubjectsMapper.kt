package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.Subject
import io.github.wulkanowy.sdk.scrapper.attendance.Subject as ScrapperSubject

internal fun List<ScrapperSubject>.mapSubjects(): List<Subject> = map {
    Subject(
        id = it.value,
        name = it.name,
    )
}

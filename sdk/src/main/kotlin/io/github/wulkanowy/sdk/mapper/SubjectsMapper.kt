package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.Subject
import io.github.wulkanowy.sdk.hebe.models.Subject as HebeSubject
import io.github.wulkanowy.sdk.scrapper.attendance.Subject as ScrapperSubject

@JvmName("mapScrapperSubject")
internal fun List<ScrapperSubject>.mapSubjects(): List<Subject> = map {
    Subject(
        id = it.value,
        name = it.name,
    )
}

@JvmName("mapHebeSubject")
internal fun List<HebeSubject>.mapSubjects(): List<Subject> = map {
    Subject(
        id = it.id,
        name = it.name,
    )
}

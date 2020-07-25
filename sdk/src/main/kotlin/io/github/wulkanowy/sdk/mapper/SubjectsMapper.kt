package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.Subject
import io.github.wulkanowy.sdk.mobile.dictionaries.Subject as ApiSubject
import io.github.wulkanowy.sdk.scrapper.attendance.Subject as ScrapperSubject

@JvmName("mapApiSubjects")
fun List<ApiSubject>.mapSubjects() = listOf(Subject(-1, "Wszystkie")) + filter { it.active }.map {
    Subject(
        id = it.id,
        name = it.name
    )
}

fun List<ScrapperSubject>.mapSubjects() = map {
    Subject(
        id = it.value,
        name = it.name
    )
}

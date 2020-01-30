package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.Subject
import io.github.wulkanowy.sdk.scrapper.attendance.Subject as ScrapperSubject
import io.github.wulkanowy.sdk.mobile.dictionaries.Subject as ApiSubject

@JvmName("mapApiSubjects")
fun List<ApiSubject>.mapSubjects(): List<Subject> {
    return listOf(Subject(-1, "Wszystkie")) + filter { it.active }.map {
        Subject(
            id = it.id,
            name = it.name
        )
    }
}

fun List<ScrapperSubject>.mapSubjects(): List<Subject> {
    return map {
        Subject(
            id = it.value,
            name = it.name
        )
    }
}

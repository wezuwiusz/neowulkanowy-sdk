package io.github.wulkanowy.sdk.dictionaries

import io.github.wulkanowy.sdk.pojo.Subject
import io.github.wulkanowy.sdk.dictionaries.Subject as ApiSubject
import io.github.wulkanowy.api.attendance.Subject as ScrapperSubject

@JvmName("mapApiSubjects")
fun List<ApiSubject>.mapSubjects(): List<Subject> {
    return map {
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

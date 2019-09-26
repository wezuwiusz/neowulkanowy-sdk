package io.github.wulkanowy.api.school

import io.github.wulkanowy.api.getEmptyIfDash

fun SchoolAndTeachersResponse.mapToTeachers(): List<Teacher> {
    return teachers.map { item ->
        item.name.split(",").map { namePart ->
            item.copy(
                name = namePart.substringBefore(" [").getEmptyIfDash().trim(),
                short = namePart.substringAfter("[").substringBefore("]").getEmptyIfDash(),
                subject = item.subject.trim()
            )
        }.asReversed()
    }.flatten().sortedWith(compareBy({ it.subject }, { it.name }))
}

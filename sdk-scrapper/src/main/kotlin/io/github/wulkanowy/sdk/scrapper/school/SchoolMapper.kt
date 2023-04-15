package io.github.wulkanowy.sdk.scrapper.school

import io.github.wulkanowy.sdk.scrapper.getEmptyIfDash

internal fun SchoolAndTeachersResponse.mapToSchool() = school.copy(
    name = school.name.trim('-'),
    address = school.address.trim('-'),
    contact = school.contact.trim('-'),
    headmaster = school.headmaster.trim('-'),
    pedagogue = school.pedagogue.trim('-'),
)

internal fun SchoolAndTeachersResponse.mapToTeachers() = teachers.map { item ->
    item.name.split(",").map { namePart ->
        item.copy(
            name = namePart.substringBefore(" [").getEmptyIfDash().trim(),
            subject = item.subject.trim(),
        ).apply {
            short = namePart.substringAfter("[").substringBefore("]").getEmptyIfDash()
        }
    }.asReversed()
}.flatten().sortedWith(compareBy({ it.subject }, { it.name }))

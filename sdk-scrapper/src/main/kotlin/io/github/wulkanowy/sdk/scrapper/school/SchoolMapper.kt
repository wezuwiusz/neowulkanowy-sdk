package io.github.wulkanowy.sdk.scrapper.school

import io.github.wulkanowy.sdk.scrapper.getEmptyIfDash

fun SchoolAndTeachersResponse.mapToSchool() = school.copy(
    name = school.name.trim('-'),
    address = school.address.trim('-'),
    contact = school.contact.trim('-'),
    headmaster = school.headmaster.trim('-'),
    pedagogue = school.pedagogue.trim('-')
)

fun SchoolAndTeachersResponse.mapToTeachers() = teachers.map { item ->
    item.name.split(",").map { namePart ->
        item.copy(
            name = namePart.substringBefore(" [").getEmptyIfDash().trim(),
            subject = item.subject.trim()
        ).apply {
            short = namePart.substringAfter("[").substringBefore("]").getEmptyIfDash()
        }
    }.asReversed()
}.flatten().sortedWith(compareBy({ it.subject }, { it.name }))

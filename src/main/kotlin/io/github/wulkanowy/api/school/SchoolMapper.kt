package io.github.wulkanowy.api.school

fun SchoolAndTeachersResponse.mapToTeachers(): List<Teacher> {
    return teachers.map { item ->
        item.name?.split(",")?.map { namePart ->
            val name = namePart.substringBefore(" [").getNullIfDash()
            val short = namePart.substringAfter("[").substringBefore("]").getNullIfDash()

            val subject = item.subject.let {
                if (it.isNullOrBlank()) null
                else it
            }

            item.copy(
                name = name?.trim(),
                short = short,
                subject = subject
            )
        }?.asReversed() ?: listOf(item)
    }.flatten().sortedWith(compareBy({ it.subject }, { it.name }))
}

private fun String.getNullIfDash(): String? {
    return if (this == "-") null
    else this
}

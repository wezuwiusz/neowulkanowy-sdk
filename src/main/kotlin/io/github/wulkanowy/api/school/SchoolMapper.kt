package io.github.wulkanowy.api.school

fun SchoolAndTeachersResponse.mapToTeachers(): List<Teacher> {
    return teachers.map { teacher ->
        val name = teacher.name?.substringBefore(" [")?.let {
            if (it == "-") null
            else it
        }
        val short = teacher.name?.substringAfter("[")?.substringBefore("]")?.let {
            if (it == "-") null
            else it
        }

        val subject = teacher.subject.let {
            if (it.isNullOrBlank()) null
            else it
        }

        teacher.copy(
            name = name,
            short = short,
            subject = subject
        )
    }.sortedWith(compareBy({ it.subject }, { it.name }))
}

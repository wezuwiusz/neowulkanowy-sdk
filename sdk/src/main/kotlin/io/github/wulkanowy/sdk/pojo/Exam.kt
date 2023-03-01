package io.github.wulkanowy.sdk.pojo

import java.time.LocalDate

data class Exam(
    val date: LocalDate,
    val entryDate: LocalDate,
    val description: String,
    @Deprecated("this property is missing in 21.09.0002.46067 update")
    val group: String,
    val subject: String,
    val teacher: String,
    val teacherSymbol: String,
    val type: String,
)

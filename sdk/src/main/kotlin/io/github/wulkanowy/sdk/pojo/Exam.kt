package io.github.wulkanowy.sdk.pojo

import java.time.LocalDate

data class Exam(
    val date: LocalDate,
    val entryDate: LocalDate,
    val description: String,
    val subject: String,
    val teacher: String,
    val teacherSymbol: String,
    val type: String,
)

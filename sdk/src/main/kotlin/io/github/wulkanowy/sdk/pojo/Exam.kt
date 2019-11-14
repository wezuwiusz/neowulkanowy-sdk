package io.github.wulkanowy.sdk.pojo

import org.threeten.bp.LocalDate

data class Exam(
    val date: LocalDate,
    val entryDate: LocalDate,
    val description: String,
    val group: String,
    val subject: String,
    val teacher: String,
    val teacherSymbol: String,
    val type: String
)

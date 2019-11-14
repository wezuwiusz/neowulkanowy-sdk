package io.github.wulkanowy.sdk.pojo

import org.threeten.bp.LocalDate

data class CompletedLesson(
    val date: LocalDate,
    val number: Int,
    val subject: String,
    val topic: String,
    val teacher: String,
    val teacherSymbol: String,
    val substitution: String,
    val absence: String,
    val resources: String
)

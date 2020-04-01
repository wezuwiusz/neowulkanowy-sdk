package io.github.wulkanowy.sdk.pojo

import org.threeten.bp.LocalDate

data class Homework(
    val date: LocalDate,
    val entryDate: LocalDate,
    val subject: String,
    val content: String,
    val teacher: String,
    val teacherSymbol: String,
    val attachments: List<HomeworkAttachment>
)

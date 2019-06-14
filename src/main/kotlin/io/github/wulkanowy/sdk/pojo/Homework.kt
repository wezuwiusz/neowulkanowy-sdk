package io.github.wulkanowy.sdk.pojo

import org.threeten.bp.LocalDate

data class Homework(
    var date: LocalDate,
    var entryDate: LocalDate,
    var subject: String,
    var content: String,
    var teacher: String,
    var teacherSymbol: String
)

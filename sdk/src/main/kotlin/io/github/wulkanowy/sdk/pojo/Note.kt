package io.github.wulkanowy.sdk.pojo

import org.threeten.bp.LocalDate

data class Note(
    var date: LocalDate,
    var teacher: String,
    var teacherSymbol: String,
    var category: String,
    var categoryType: Int,
    var showPoints: Boolean,
    var points: String,
    var content: String
)

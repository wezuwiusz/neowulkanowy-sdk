package io.github.wulkanowy.sdk.pojo

import io.github.wulkanowy.sdk.scrapper.notes.Note
import org.threeten.bp.LocalDate

data class Note(
    var date: LocalDate,
    var teacher: String,
    var teacherSymbol: String,
    var category: String,
    var categoryType: Note.CategoryType,
    var showPoints: Boolean,
    var points: Int,
    var content: String
)

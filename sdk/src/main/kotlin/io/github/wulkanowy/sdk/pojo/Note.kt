package io.github.wulkanowy.sdk.pojo

import io.github.wulkanowy.sdk.scrapper.notes.NoteCategory
import java.time.LocalDate

data class Note(
    var date: LocalDate,
    var teacher: String,
    var teacherSymbol: String,
    var category: String,
    var categoryType: NoteCategory,
    var showPoints: Boolean,
    var points: Int,
    var content: String,
)

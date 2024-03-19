package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.Note
import io.github.wulkanowy.sdk.scrapper.notes.NoteCategory
import io.github.wulkanowy.sdk.scrapper.notes.Note as ScrapperNote

internal fun List<ScrapperNote>.mapNotes() = map {
    Note(
        date = it.date.toLocalDate(),
        teacher = it.teacher,
        teacherSymbol = it.teacherSymbol,
        category = it.category.orEmpty(),
        categoryType = NoteCategory.getByValue(it.categoryType ?: NoteCategory.UNKNOWN.id),
        showPoints = it.showPoints,
        points = it.points.toIntOrNull() ?: 0,
        content = it.content,
    )
}

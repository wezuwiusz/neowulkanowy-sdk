package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.Note
import io.github.wulkanowy.sdk.scrapper.notes.NoteCategory
import io.github.wulkanowy.sdk.hebe.models.Note as HebeNote
import io.github.wulkanowy.sdk.scrapper.notes.Note as ScrapperNote

@JvmName("mapScrapperNotes")
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

@JvmName("mapHebeNotes")
internal fun List<HebeNote>.mapNotes() = map {
    Note(
        date = it.dateValid.date,
        teacher = it.creator.displayName,
        teacherSymbol = it.creator.name
            .first()
            .toString() + it.creator.surname
            .first()
            .toString(),
        category = it.category?.name ?: "Bez kategorii",
        categoryType = NoteCategory.getByValue(it.category?.id ?: NoteCategory.UNKNOWN.id),
        showPoints = it.points != null,
        points = it.points ?: 0,
        content = it.content,
    )
}

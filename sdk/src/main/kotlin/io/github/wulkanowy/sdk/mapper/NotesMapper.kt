package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.scrapper.toLocalDate
import io.github.wulkanowy.sdk.mobile.dictionaries.Dictionaries
import io.github.wulkanowy.sdk.pojo.Note
import io.github.wulkanowy.sdk.toLocalDate
import io.github.wulkanowy.sdk.scrapper.notes.Note as ScrapperNote
import io.github.wulkanowy.sdk.mobile.notes.Note as ApiNote

fun List<ApiNote>.mapNotes(dictionaries: Dictionaries): List<Note> {
    return map {
        Note(
            date = it.entryDate.toLocalDate(),
            content = it.content,
            teacherSymbol = dictionaries.teachers.singleOrNull { teacher -> teacher.id == it.employeeId }?.code.orEmpty(),
            teacher = "${it.employeeName} ${it.employeeSurname}",
            category = dictionaries.noteCategories.singleOrNull { cat -> cat.id == it.noteCategoryId }?.name.orEmpty(),
            categoryType = ScrapperNote.CategoryType.UNKNOWN,
            showPoints = false,
            points = 0
        )
    }
}

fun List<ScrapperNote>.mapNotes(): List<Note> {
    return map {
        Note(
            date = it.date.toLocalDate(),
            teacher = it.teacher,
            teacherSymbol = it.teacherSymbol,
            category = it.category,
            categoryType = ScrapperNote.CategoryType.getByValue(it.categoryType),
            showPoints = it.showPoints,
            points = it.points.toIntOrNull() ?: 0,
            content = it.content
        )
    }
}

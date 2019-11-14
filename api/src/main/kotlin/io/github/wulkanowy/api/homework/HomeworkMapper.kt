package io.github.wulkanowy.api.homework

import io.github.wulkanowy.api.toDate
import io.github.wulkanowy.api.toLocalDate
import org.threeten.bp.LocalDate

fun List<HomeworkResponse>.mapHomeworkList(startDate: LocalDate, endDate: LocalDate?): List<Homework> {
    val end = endDate ?: startDate
    return asSequence().map { day ->
        day.items.map {
            val teacherAndDate = it.teacher.split(", ")
            it.apply {
                date = day.date
                entryDate = teacherAndDate.last().toDate("dd.MM.yyyy")
                teacher = teacherAndDate.first().split(" [").first()
                teacherSymbol = teacherAndDate.first().split(" [").last().removeSuffix("]")
            }
        }
    }.flatten().filter {
        it.date.toLocalDate() in startDate..end
    }.sortedWith(compareBy({ it.date }, { it.subject })).toList()
}

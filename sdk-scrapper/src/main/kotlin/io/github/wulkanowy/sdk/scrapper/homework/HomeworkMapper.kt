package io.github.wulkanowy.sdk.scrapper.homework

import io.github.wulkanowy.sdk.scrapper.toLocalDate
import java.time.LocalDate

fun List<HomeworkDay>.mapHomework(startDate: LocalDate, endDate: LocalDate?): List<Homework> {
    val end = endDate ?: startDate
    return asSequence().map { day ->
        day.items.map { homework ->
            val teacherAndDate = homework.teacher.split(", ")
            homework.copy(
                teacher = teacherAndDate.first().split(" [").first()
            ).apply {
                teacherSymbol = teacherAndDate.first().split(" [").last().removeSuffix("]")
                _attachments = homework.attachments.map {
                    it.url to it.filename
                }
            }
        }
    }.flatten().filter {
        it.date.toLocalDate() in startDate..end
    }.sortedWith(compareBy({ it.date }, { it.subject })).toList()
}

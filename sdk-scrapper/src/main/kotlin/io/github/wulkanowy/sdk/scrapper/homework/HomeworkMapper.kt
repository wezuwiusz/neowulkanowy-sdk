package io.github.wulkanowy.sdk.scrapper.homework

import io.github.wulkanowy.sdk.scrapper.toLocalDate
import java.time.LocalDate

fun List<HomeworkDay>.mapHomework(startDate: LocalDate, endDate: LocalDate?): List<Homework> {
    val end = endDate ?: startDate
    return asSequence().map { day ->
        day.items.map { homework ->
            val teacherAndDate = homework.teacher.split(", ")
            val teacher = teacherAndDate.first().split(" [").first()
            val teacherCode = teacherAndDate.first().split(" [").last().removeSuffix("]")
            homework.copy(
                teacher = teacher,
                date = day.date,
            ).apply {
                teacherSymbol = teacherCode
                _attachments = homework.attachments.map { it.url to it.filename }
            }
        }
    }.flatten().filter {
        it.date.toLocalDate() in startDate..end
    }.sortedWith(compareBy({ it.date }, { it.subject })).toList()
}

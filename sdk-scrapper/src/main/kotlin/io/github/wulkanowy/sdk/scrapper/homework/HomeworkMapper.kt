package io.github.wulkanowy.sdk.scrapper.homework

import io.github.wulkanowy.sdk.scrapper.toDate
import io.github.wulkanowy.sdk.scrapper.toLocalDate
import org.jsoup.Jsoup
import org.threeten.bp.LocalDate

fun List<HomeworkResponse>.mapHomeworkList(startDate: LocalDate, endDate: LocalDate?): List<Homework> {
    val end = endDate ?: startDate
    return asSequence().map { day ->
        day.items.map { homework ->
            val teacherAndDate = homework.teacher.split(", ")
            homework.apply {
                date = day.date
                entryDate = teacherAndDate.last().toDate("dd.MM.yyyy")
                teacher = teacherAndDate.first().split(" [").first()
                teacherSymbol = teacherAndDate.first().split(" [").last().removeSuffix("]")
                _attachments = attachments.map {
                    Jsoup.parse(it).select("a").let { link -> link.attr("href") to link.text() }
                }
            }
        }
    }.flatten().filter {
        it.date.toLocalDate() in startDate..end
    }.sortedWith(compareBy({ it.date }, { it.subject })).toList()
}

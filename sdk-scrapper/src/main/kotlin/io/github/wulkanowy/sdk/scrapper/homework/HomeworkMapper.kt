package io.github.wulkanowy.sdk.scrapper.homework

import io.github.wulkanowy.sdk.scrapper.toLocalDate
import org.jsoup.parser.Parser
import java.time.LocalDate

fun List<HomeworkDay>.mapHomework(startDate: LocalDate, endDate: LocalDate?): List<Homework> {
    val end = endDate ?: startDate
    return asSequence().map { day ->
        day.items.map { homework ->
            val teacherAndCode = homework.teacher.split(", ").first().split(" [")
            val teacher = teacherAndCode.first()
            val teacherCode = teacherAndCode.last().removeSuffix("]")
            homework.copy(
                teacher = teacher,
                date = day.date,
                content = Parser.unescapeEntities(homework.content, true)
            ).apply {
                teacherSymbol = teacherCode
                _attachments = homework.attachments.map { it.url to it.filename }
            }
        }
    }.flatten().filter {
        it.date.toLocalDate() in startDate..end
    }.sortedWith(compareBy({ it.date }, { it.subject })).toList()
}

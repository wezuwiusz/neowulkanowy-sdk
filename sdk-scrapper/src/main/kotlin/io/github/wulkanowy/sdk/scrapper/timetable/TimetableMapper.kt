package io.github.wulkanowy.sdk.scrapper.timetable

import io.github.wulkanowy.sdk.scrapper.ApiResponse
import io.github.wulkanowy.sdk.scrapper.toDate
import io.github.wulkanowy.sdk.scrapper.toFormat
import io.github.wulkanowy.sdk.scrapper.toLocalDate
import org.jsoup.Jsoup
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val parser = TimetableParser()
private val formatter1 = DateTimeFormatter.ofPattern("dd.MM.yyyy")
private val formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

fun TimetableResponse.mapTimetableList(startDate: LocalDate, endDate: LocalDate?) = rows.flatMap { lessons ->
    lessons.drop(1).mapIndexed { i, it ->
        val times = lessons[0].split("<br />")
        val header = headers.drop(1)[i].date.split("<br />")
        val date = LocalDate.parse(header[1], formatter1)
        TimetableCell(
            date = date,
            start = LocalDateTime.parse("${date.toFormat("yyyy-MM-dd")} ${times[1]}", formatter2),
            end = LocalDateTime.parse("${date.toFormat("yyyy-MM-dd")} ${times[2]}", formatter2),
            number = times[0].toInt(),
            td = Jsoup.parse(it)
        )
    }.mapNotNull { parser.getTimetable(it) }
}.asSequence().filter {
    it.date >= startDate && it.date <= (endDate ?: startDate.plusDays(4))
}.sortedWith(compareBy({ it.date }, { it.number })).toList()

fun TimetableResponse.mapTimetableHeaders() = headers.drop(1).map {
    val header = it.date.split("<br />")
    TimetableDayHeader(
        date = header[1].toDate("dd.MM.yyyy").toLocalDate(),
        content = header.drop(2).joinToString(separator = "<br />")
    )
}

fun TimetableResponse.mapTimetableAdditional() = additional.flatMap { day ->
    val date = LocalDate.parse(day.header.substringAfter(", "), formatter1)
    day.descriptions.map { lesson ->
        val description = Jsoup.parse(lesson.description).text()
        val startTime = description.substringBefore(" - ")
        val endTime = description.split(" ")[2]
        TimetableAdditional(
            date = date,
            start = LocalDateTime.parse("${date.toFormat("yyyy-MM-dd")} $startTime", formatter2),
            end = LocalDateTime.parse("${date.toFormat("yyyy-MM-dd")} $endTime", formatter2),
            subject = description.substringAfter("$endTime ")
        )
    }
}

fun ApiResponse<Map<String, List<CompletedLesson>>>.mapCompletedLessonsList(start: LocalDate, endDate: LocalDate?): List<CompletedLesson> {
    return data.orEmpty()
        .flatMap { it.value }
        .map {
            it.copy(
                teacherSymbol = it.teacher?.substringAfter(" [")?.substringBefore("]"),
                teacher = it.teacher?.substringBefore(" ["),
            )
        }.sortedWith(compareBy({ it.date }, { it.number })).toList().filter {
            it.date.toLocalDate() >= start && it.date.toLocalDate() <= (endDate ?: start.plusDays(4))
        }
}

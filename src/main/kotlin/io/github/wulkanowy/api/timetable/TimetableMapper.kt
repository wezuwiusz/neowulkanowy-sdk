package io.github.wulkanowy.api.timetable

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import io.github.wulkanowy.api.ApiResponse
import io.github.wulkanowy.api.toDate
import io.github.wulkanowy.api.toFormat
import io.github.wulkanowy.api.toLocalDate
import org.jsoup.Jsoup
import org.threeten.bp.LocalDate

fun TimetableResponse.mapTimetableList(startDate: LocalDate, endDate: LocalDate?): List<Timetable> {
    return rows2api.flatMap { lessons ->
        lessons.drop(1).mapIndexed { i, it ->
            val times = lessons[0].split("<br />")
            TimetableResponse.TimetableRow.TimetableCell().apply {
                date = header.drop(1)[i].date.split("<br />")[1].toDate("dd.MM.yyyy")
                start = "${date.toLocalDate().toFormat("yyyy-MM-dd")} ${times[1]}".toDate("yyyy-MM-dd HH:mm")
                end = "${date.toLocalDate().toFormat("yyyy-MM-dd")} ${times[2]}".toDate("yyyy-MM-dd HH:mm")
                number = times[0].toInt()
                td = Jsoup.parse(it)
            }
        }.mapNotNull { TimetableParser().getTimetable(it) }
    }.asSequence().filter {
        it.date.toLocalDate() >= startDate && it.date.toLocalDate() <= endDate ?: startDate.plusDays(4)
    }.sortedWith(compareBy({ it.date }, { it.number })).toList()
}

fun ApiResponse<*>.mapCompletedLessonsList(start: LocalDate, endDate: LocalDate?, gson: GsonBuilder): List<CompletedLesson> {
    return (data as LinkedTreeMap<*, *>).map { list ->
        gson.create().fromJson<List<CompletedLesson>>(Gson().toJson(list.value), object : TypeToken<ArrayList<CompletedLesson>>() {}.type)
    }.flatten().map {
        it.apply {
            teacherSymbol = teacher.substringAfter(" [").substringBefore("]")
            teacher = teacher.substringBefore(" [")
        }
    }.sortedWith(compareBy({ it.date }, { it.number })).toList().filter {
        it.date.toLocalDate() >= start && it.date.toLocalDate() <= endDate
    }
}

package io.github.wulkanowy.sdk.scrapper

import org.jsoup.Jsoup.parse
import java.text.Normalizer
import java.text.SimpleDateFormat
import java.time.DayOfWeek.MONDAY
import java.time.Instant.ofEpochMilli
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId.systemDefault
import java.time.format.DateTimeFormatter.ofPattern
import java.time.temporal.TemporalAdjusters.previousOrSame
import java.util.Date
import kotlin.math.roundToInt

fun String.toDate(format: String): Date = SimpleDateFormat(format).parse(this)

fun Date.toLocalDate(): LocalDate = ofEpochMilli(time).atZone(systemDefault()).toLocalDate()

fun LocalDate.toDate(): Date = Date.from(atStartOfDay(systemDefault()).toInstant())

fun LocalDate.toFormat(format: String): String = format(ofPattern(format))

fun LocalDateTime.toFormat(format: String): String = format(ofPattern(format))

fun LocalDate.getLastMonday(): LocalDate = with(previousOrSame(MONDAY))

fun LocalDate.getSchoolYear(): Int = if (month.value > 8) year else year - 1

fun getGradeShortValue(value: String?): String {
    return when (value) {
        "celujący" -> "6"
        "bardzo dobry" -> "5"
        "dobry" -> "4"
        "dostateczny" -> "3"
        "dopuszczający" -> "2"
        "niedostateczny" -> "1"
        else -> value ?: ""
    }
}

fun String.getEmptyIfDash(): String {
    return if (this == "-") ""
    else this
}

fun String.getGradePointPercent(): String {
    return split("/").let { (student, max) ->
        if (max == "0") return this
        "${(student.toDouble() / max.toDouble() * 100).roundToInt()}%"
    }
}

fun getScriptParam(name: String, content: String, fallback: String = ""): String {
    return "$name: '(.)*'".toRegex().find(content).let { result ->
        if (null !== result) parse(result.groupValues[0].substringAfter("'").substringBefore("'")).text() else fallback
    }
}

fun String.getNormalizedSymbol(): String {
    return trim().lowercase().replace("default", "").run {
        Normalizer.normalize(this, Normalizer.Form.NFD).run {
            "\\p{InCombiningDiacriticalMarks}+".toRegex().replace(this, "")
        }
    }.replace("[^a-z0-9]".toRegex(), "").ifBlank { "Default" }
}

fun String.capitalise() = replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

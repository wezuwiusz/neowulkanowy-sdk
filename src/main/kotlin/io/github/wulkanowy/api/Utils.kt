package io.github.wulkanowy.api

import org.jsoup.Jsoup.parse
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.DayOfWeek.MONDAY
import org.threeten.bp.Instant.ofEpochMilli
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId.systemDefault
import org.threeten.bp.format.DateTimeFormatter.ofPattern
import org.threeten.bp.temporal.TemporalAdjusters.previousOrSame
import java.text.Normalizer
import java.text.SimpleDateFormat
import java.util.Date

fun String.toDate(format: String): Date = SimpleDateFormat(format).parse(this)

fun Date.toLocalDate(): LocalDate = ofEpochMilli(time).atZone(systemDefault()).toLocalDate()

fun LocalDate.toDate(): Date = DateTimeUtils.toDate(atStartOfDay(systemDefault()).toInstant())

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

fun getScriptParam(name: String, content: String, fallback: String = ""): String {
    return "$name: '(.)*'".toRegex().find(content).let { result ->
        if (null !== result) parse(result.groupValues[0].substringAfter("'").substringBefore("'")).text() else fallback
    }
}

fun String.getNormalizedSymbol(): String {
    return trim().toLowerCase().replace("default", "").run {
        Normalizer.normalize(this, Normalizer.Form.NFD).run {
            "\\p{InCombiningDiacriticalMarks}+".toRegex().replace(this, "")
        }
    }.replace("[^a-z0-9]".toRegex(), "").ifBlank { "Default" }
}

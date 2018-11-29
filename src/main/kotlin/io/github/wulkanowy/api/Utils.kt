package io.github.wulkanowy.api

import org.threeten.bp.DayOfWeek
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.TemporalAdjusters
import java.text.SimpleDateFormat
import java.util.*

fun String.toDate(format: String): Date = SimpleDateFormat(format).parse(this)

fun Date.toLocalDate(): LocalDate = Instant.ofEpochMilli(this.time).atZone(ZoneId.systemDefault()).toLocalDate()

fun LocalDate.toDate(): Date = java.sql.Date.valueOf(this.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))

fun LocalDate.toFormat(format: String): String = this.format(DateTimeFormatter.ofPattern(format))

fun LocalDate.getLastMonday(): LocalDate = this.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

fun getGradeShortValue(value: String): String {
    return when (value) {
        "celujący" -> "6"
        "bardzo dobry" -> "5"
        "dobry" -> "4"
        "dostateczny" -> "3"
        "dopuszczający" -> "2"
        "niedostateczny" -> "1"
        else -> value
    }
}

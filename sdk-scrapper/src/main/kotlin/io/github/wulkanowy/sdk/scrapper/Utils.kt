package io.github.wulkanowy.sdk.scrapper

import io.github.wulkanowy.sdk.scrapper.messages.Mailbox
import io.github.wulkanowy.sdk.scrapper.messages.Recipient
import io.github.wulkanowy.sdk.scrapper.messages.RecipientType
import org.jsoup.Jsoup.parse
import java.text.Normalizer
import java.text.SimpleDateFormat
import java.time.DayOfWeek.MONDAY
import java.time.Instant.ofEpochMilli
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId.systemDefault
import java.time.format.DateTimeFormatter.ofPattern
import java.time.temporal.TemporalAdjusters.previousOrSame
import java.util.Date
import kotlin.math.roundToInt

fun String.toDate(format: String): Date = SimpleDateFormat(format).parse(this)

fun String.toLocalDate(format: String): LocalDate = LocalDate.parse(this, ofPattern(format))

fun String.toLocalTime(): LocalTime = LocalTime.parse(this)

fun Date.toLocalDate(): LocalDate = ofEpochMilli(time).atZone(systemDefault()).toLocalDate()

fun LocalDate.toDate(): Date = Date.from(atStartOfDay(systemDefault()).toInstant())

fun LocalDate.toFormat(format: String): String = format(ofPattern(format))

fun LocalDateTime.toFormat(format: String): String = format(ofPattern(format))

fun LocalDate.getLastMonday(): LocalDate = with(previousOrSame(MONDAY))

fun LocalDate.getSchoolYear(): Int = if (month.value > 8) year else year - 1

fun getGradeShortValue(value: String?): String {
    return when (value?.trim()) {
        "celujący" -> "6"
        "bardzo dobry" -> "5"
        "dobry" -> "4"
        "dostateczny" -> "3"
        "dopuszczający" -> "2"
        "niedostateczny" -> "1"
        else -> value.orEmpty().trim()
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

fun List<Recipient>.normalizeRecipients() = map { it.parseName() }

fun Recipient.parseName(): Recipient {
    val typeSeparatorPosition = name.indexOfAny(RecipientType.values().map { " - ${it.letter} - " })

    val userName = name.substring(0..typeSeparatorPosition).trim()
    val typeLetter = name.substring(typeSeparatorPosition..typeSeparatorPosition + 3 * 2 + 1).substringAfter(" - ").substringBefore(" - ")
    val studentName = name.substringAfter(" - $typeLetter - ").substringBefore(" - (")
    val schoolName = name.substringAfter("(").trimEnd(')')
    return copy(
        name = userName,
        type = typeLetter.let { letter -> RecipientType.values().first { it.letter == letter } },
        schoolNameShort = schoolName,
        studentName = studentName.takeIf { it != "($schoolName)" } ?: userName,
    )
}

fun Mailbox.toRecipient() = Recipient(
    mailboxGlobalKey = globalKey,
    studentName = studentName,
    name = name,
    schoolNameShort = schoolNameShort,
)

fun Recipient.toMailbox() = Mailbox(
    globalKey = mailboxGlobalKey,
    name = name,
    userType = -1,
    studentName = studentName,
    schoolNameShort = schoolNameShort,
)

fun String.capitalise() = replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

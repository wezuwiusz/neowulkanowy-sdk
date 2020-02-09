package io.github.wulkanowy.sdk

import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter.ofPattern
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

fun String.toLocalDateTime(format: String) = LocalDateTime.parse(this, ofPattern(format))

fun Long.toLocalDate(): LocalDate = Instant
    .ofEpochMilli(this * 1000L)
    .atZone(ZoneId.systemDefault())
    .toLocalDate()

fun Long.toLocalDateTime(): LocalDateTime = Instant
    .ofEpochMilli(this * 1000L)
    .atZone(ZoneId.systemDefault())
    .toLocalDateTime()

fun Date.toLocalDateTime(): LocalDateTime = DateTimeUtils.toLocalDateTime(Timestamp(time))

fun String.normalizeRecipient() = substringBeforeLast("-").substringBefore(" [").substringBeforeLast(" (").trim()

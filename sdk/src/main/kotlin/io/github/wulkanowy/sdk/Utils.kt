package io.github.wulkanowy.sdk

import org.threeten.bp.DateTimeUtils
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter.ofPattern
import java.sql.Timestamp
import java.util.Date

fun String.toLocalDateTime(format: String): LocalDateTime = LocalDateTime.parse(this, ofPattern(format))

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

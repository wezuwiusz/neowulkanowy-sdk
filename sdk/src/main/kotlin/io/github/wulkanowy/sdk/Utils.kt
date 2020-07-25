package io.github.wulkanowy.sdk

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId.systemDefault
import java.time.format.DateTimeFormatter.ofPattern
import java.util.Date

fun String.toLocalDateTime(format: String): LocalDateTime = LocalDateTime.parse(this, ofPattern(format))

fun Long.toLocalDate(): LocalDate = Instant
    .ofEpochMilli(this * 1000L)
    .atZone(systemDefault())
    .toLocalDate()

fun Long.toLocalDateTime(): LocalDateTime = Instant
    .ofEpochMilli(this * 1000L)
    .atZone(systemDefault())
    .toLocalDateTime()

fun Date.toLocalDateTime(): LocalDateTime = Instant
    .ofEpochMilli(time)
    .atZone(systemDefault())
    .toLocalDateTime()

fun String.normalizeRecipient() = substringBeforeLast("-").substringBefore(" [").substringBeforeLast(" (").trim()

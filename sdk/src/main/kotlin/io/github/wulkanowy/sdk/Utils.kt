package io.github.wulkanowy.sdk

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter.ofPattern
import java.util.Date

private val registerTimeZone = ZoneId.of("Europe/Warsaw")

fun String.toLocalDateTime(format: String): LocalDateTime = LocalDateTime.parse(this, ofPattern(format))

fun Long.toLocalDate(): LocalDate = Instant
    .ofEpochMilli(this * 1000L)
    .atZone(registerTimeZone)
    .toLocalDate()

fun Long.toLocalDateTime(): LocalDateTime = Instant
    .ofEpochMilli(this * 1000L)
    .atZone(registerTimeZone)
    .toLocalDateTime()

fun String.normalizeRecipient() = substringBeforeLast("-")
    .substringBefore(" [")
    .substringBeforeLast(" (")
    .trim()

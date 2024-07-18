package io.github.wulkanowy.sdk

import io.github.wulkanowy.sdk.pojo.MailboxType
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter.ofPattern

private val registerTimeZone = ZoneId.of("Europe/Warsaw")

fun String.toLocalDateTime(format: String): LocalDateTime = LocalDateTime.parse(this, ofPattern(format))

fun Long.toLocalDate(): LocalDate = Instant
    .ofEpochMilli(this)
    .atZone(registerTimeZone)
    .toLocalDate()

fun Long.toLocalDateTime(): LocalDateTime = Instant
    .ofEpochMilli(this)
    .atZone(registerTimeZone)
    .toLocalDateTime()

fun String.normalizeRecipient() = substringBeforeLast("-")
    .substringBefore(" [")
    .substringBeforeLast(" (")
    .trim()

fun String.extractNameFromRecipient(): String = substringBeforeLast(" - ")
    .substringBeforeLast(" - ")
    .trim()

fun String.extractSchoolShortFromRecipient(): String = substringAfterLast(" - ")
    .replace("(", "")
    .replace(")", "")
    .trim()

fun String.extractTypeFromRecipient(): MailboxType = MailboxType
    .fromLetter(
        substringBeforeLast(" - ")
            .substringAfterLast(" - ")
            .trim(),
    )

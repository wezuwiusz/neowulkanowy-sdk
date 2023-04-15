package io.github.wulkanowy.sdk.scrapper.conferences

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

internal fun List<Conference>.mapConferences(): List<Conference> = map {
    val dateString = it.title.split(",")[1].trim().replace(" godzina", "")
    it.copy(
        title = it.title.substringAfter(", ").substringAfter(", "),
        date = LocalDateTime.parse(dateString, dateFormatter),
    )
}.sortedBy { it.date }

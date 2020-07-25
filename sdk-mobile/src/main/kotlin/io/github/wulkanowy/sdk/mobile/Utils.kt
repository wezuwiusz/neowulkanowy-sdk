package io.github.wulkanowy.sdk.mobile

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ofPattern

fun LocalDate.toFormat(): String = format(ofPattern("yyyy-MM-dd"))

fun LocalDateTime.toFormat(): String = format(ofPattern("yyyy-MM-dd"))

package io.github.wulkanowy.sdk.mobile

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter.ofPattern

fun LocalDate.toFormat(): String = format(ofPattern("yyyy-MM-dd"))

fun LocalDateTime.toFormat(): String = format(ofPattern("yyyy-MM-dd"))

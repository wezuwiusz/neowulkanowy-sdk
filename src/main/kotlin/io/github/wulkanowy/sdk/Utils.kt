package io.github.wulkanowy.sdk

import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId

fun Long.toLocalDate(): LocalDate = Instant
        .ofEpochMilli(this * 1000L)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

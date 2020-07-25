package io.github.wulkanowy.sdk.scrapper

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date

open class BaseTest {

    fun getLocalDate(year: Int, month: Int, day: Int): LocalDate {
        return LocalDate.of(year, month, day)
    }

    fun getLocalDateTime(year: Int, month: Int, day: Int, hour: Int = 0, minute: Int = 0, second: Int = 0): LocalDateTime {
        return LocalDateTime.of(year, month, day, hour, minute, second)
    }

    fun getDate(year: Int, month: Int, day: Int, hour: Int = 0, minute: Int = 0, second: Int = 0, mili: Int = 0): Date {
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, second)
            set(Calendar.MILLISECOND, mili)
        }.time
    }
}

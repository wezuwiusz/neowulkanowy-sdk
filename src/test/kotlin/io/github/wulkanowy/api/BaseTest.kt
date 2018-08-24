package io.github.wulkanowy.api

import pl.droidsonroids.jspoon.Jspoon
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import kotlin.reflect.KClass

open class BaseTest {

    fun <T> getFixture(testClass: KClass<*>, response: Class<T>, filename: String): T {
        return Jspoon.create().adapter(response).fromHtml(testClass.java.getResource(filename).readText())
    }

    fun getDate(year: Int, month: Int, day: Int): Date {
        return Date.from(LocalDate.of(year, month, day)
                .atStartOfDay(ZoneId.systemDefault()).toInstant())
    }
}

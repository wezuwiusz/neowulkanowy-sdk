package io.github.wulkanowy.sdk.mobile.timetable

import io.github.wulkanowy.sdk.mobile.BaseLocalTest
import io.github.wulkanowy.sdk.mobile.repository.MobileRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.create
import java.time.LocalDate.of

class TimetableTest : BaseLocalTest() {

    private val timetable by lazy { MobileRepository(getRetrofit().create()) }

    @Test
    fun getTimetable() {
        server.enqueueAndStart("PlanLekcji.json")

        val items = runBlocking { timetable.getTimetable(of(2020, 1, 10), of(2020, 2, 11), 1, 2, 3) }

        assertEquals(5, items.size)
    }
}

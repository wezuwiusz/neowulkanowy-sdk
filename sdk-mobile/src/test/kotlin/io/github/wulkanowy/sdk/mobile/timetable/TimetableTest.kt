package io.github.wulkanowy.sdk.mobile.timetable

import io.github.wulkanowy.sdk.mobile.BaseLocalTest
import io.github.wulkanowy.sdk.mobile.repository.MobileRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import org.threeten.bp.LocalDate.of
import retrofit2.create

class TimetableTest : BaseLocalTest() {

    private val timetable by lazy { MobileRepository(getRetrofit().create()) }

    @Test
    fun getTimetable() {
        server.enqueueAndStart("PlanLekcji.json")

        val items = timetable.getTimetable(of(2020, 1, 10), of(2020, 2, 11), 1, 2, 3).blockingGet()

        assertEquals(5, items.size)
    }
}

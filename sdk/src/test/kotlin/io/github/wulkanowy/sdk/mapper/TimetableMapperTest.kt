package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.mobile.BaseLocalTest
import io.github.wulkanowy.sdk.mobile.timetable.TimetableTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.threeten.bp.LocalDate.of
import org.threeten.bp.LocalDateTime

class TimetableMapperTest : BaseLocalTest() {

    private val mobile by lazy {
        Sdk().apply {
            mode = Sdk.Mode.API
            mobileBaseUrl = server.url("/").toString()
        }
    }

    @Test
    fun getApiTimetable() {
        server.enqueue("Slowniki.json", BaseLocalTest::class.java)
        server.enqueueAndStart("PlanLekcji.json", TimetableTest::class.java)

        val subjects = mobile.getTimetable(of(2020, 2, 3), of(2020, 2, 4)).blockingGet()
        assertEquals(3, subjects.size)
        with(subjects[1]) {
            assertEquals(2, number)
            assertEquals(LocalDateTime.of(2020, 2, 3, 10, 45, 0), start)
            assertEquals(LocalDateTime.of(2020, 2, 3, 11, 30, 0), end)

            assertEquals("Sieci komputerowe", subject)
            assertEquals("t.infor", group)
            assertEquals("Stanisław Krupa", teacher)
            assertEquals("G1", room)
            assertEquals("przeniesiona z lekcji 1, 10.02.2020", info)
            assertEquals("Wychowanie fizyczne", subjectOld)
            assertEquals("Mateusz Kowal", teacherOld)
            assertEquals("S4", roomOld)

            assertEquals(false, canceled)
            assertEquals(true, changes)
        }
    }
}

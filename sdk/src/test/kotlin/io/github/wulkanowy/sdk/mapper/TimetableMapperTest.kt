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

        val lessons = mobile.getTimetable(of(2020, 2, 3), of(2020, 2, 4)).blockingGet()
        assertEquals(4, lessons.size)

        with(lessons[1]) {
            assertEquals(2, number)
            assertEquals(LocalDateTime.of(2020, 2, 3, 8, 55, 0), start)
            assertEquals(LocalDateTime.of(2020, 2, 3, 9, 40, 0), end)

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

    @Test
    fun getApiTimetable_canceledWithInfoAboutChange() {
        server.enqueue("Slowniki.json", BaseLocalTest::class.java)
        server.enqueueAndStart("PlanLekcji.json", TimetableTest::class.java)

        val lessons = mobile.getTimetable(of(2020, 2, 3), of(2020, 2, 4)).blockingGet()

        with(lessons[3]) {
            assertEquals(4, number)

            assertEquals("nieobecny oddział - Zwiedzanie Muzeum II Wojny Światowej w Gdańsku.", info)

            assertEquals(true, canceled)
            assertEquals(false, changes)
        }
    }
}

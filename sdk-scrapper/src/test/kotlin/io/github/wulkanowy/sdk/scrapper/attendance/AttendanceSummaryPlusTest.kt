package io.github.wulkanowy.sdk.scrapper.attendance

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import io.github.wulkanowy.sdk.scrapper.register.RegisterTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Month

class AttendanceSummaryPlusTest : BaseLocalTest() {

    private val attendance by lazy {
        runBlocking {
            getStudentPlusRepo {
                it.enqueue("Context-all-enabled.json", RegisterTest::class.java)
                it.enqueue("FrekwencjaStatystyki.json")
            }.getAttendanceSummary(1, 2, 3)
        }
    }

    @Test
    fun getAllTest() {
        val items = attendance
        assertEquals(1, items.size)
    }

    @Test
    fun attendance_march() {
        with(attendance[0]) {
            assertEquals(Month.MARCH, month)
            assertEquals(1, presence)
            assertEquals(2, absence)
            assertEquals(3, absenceExcused)
            assertEquals(6, absenceForSchoolReasons)
            assertEquals(4, lateness)
            assertEquals(5, latenessExcused)
            assertEquals(2, exemption)
        }
    }
}

package io.github.wulkanowy.api.grades

import io.github.wulkanowy.api.BaseTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GradesStatisticsTest : BaseTest() {

    private val partial by lazy {
        getSnpRepo(GradesStatisticsTest::class.java, "Statystyki-czastkowe.html").getGradesStatistics(123, false).blockingGet()
    }

    private val annual by lazy {
        getSnpRepo(GradesStatisticsTest::class.java, "Statystyki-roczne.html").getGradesStatistics(321, true).blockingGet()
    }

    @Test
    fun getGradesStatistics() {
        assertEquals(12, partial.size)
        assertEquals(6, annual.size)
    }

    @Test
    fun getGradesStatistics_empty() {
        partial[0].run {
            assertEquals("Język polski", subject)
            assertEquals("6", grade)
            assertEquals(6, gradeValue)
            assertEquals(0, amount)
        }
    }

    @Test
    fun getGradesStatistics_filled() {
        partial[3].run {
            assertEquals("Język polski", subject)
            assertEquals("3", grade)
            assertEquals(3, gradeValue)
            assertEquals(63, amount)
        }
    }

    @Test
    fun getGradeStatistics_shortValue() {
        annual[1].run {
            assertEquals("Język angielski", subject)
            assertEquals("bardzo dobry", grade)
            assertEquals(5, gradeValue)
            assertEquals(4, amount)
        }
    }
}

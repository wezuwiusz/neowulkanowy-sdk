package io.github.wulkanowy.sdk.scrapper.grades

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GradesStatisticsTest : BaseLocalTest() {

    private val partial by lazy {
        runBlocking { getStudentRepo(GradesStatisticsTest::class.java, "Statystyki-czastkowe.json").getGradesPartialStatistics(123) }
    }

    private val annual by lazy {
        runBlocking { getStudentRepo(GradesStatisticsTest::class.java, "Statystyki-roczne.json").getGradesAnnualStatistics(321) }
    }

    private val points by lazy {
        runBlocking { getStudentRepo(GradesStatisticsTest::class.java, "Statystyki-punkty.json").getGradesPointsStatistics(420) }
    }

    @Test
    fun getGradesStatistics() {
        assertEquals(3, partial.size)
        assertEquals(6, annual.size)
        assertEquals(3, points.size)
    }

    @Test
    fun getGradesStatistics_empty() {
        with(partial[0]) {
            assertEquals("Język polski", subject)
            with(items[0]) {
                assertEquals(6, grade)
                assertEquals(0, amount)
            }
        }
    }

    @Test
    fun getGradesStatistics_filled() {
        with(partial[0]) {
            assertEquals("Język polski", subject)
            with(items[3]) {
                assertEquals(3, grade)
                assertEquals(63, amount)
            }
        }
    }

    @Test
    fun getGradeStatistics_shortValue() {
        with(annual[1]) {
            assertEquals("Język angielski", subject)
            assertEquals(5, grade)
            assertEquals(4, amount)
        }
    }

    @Test
    fun getGradeStatisticsPoints() {
        with(points[0]) {
            assertEquals("Edukacja dla bezpieczeństwa", subject)
            assertEquals(80.0, student, .0)
            assertEquals(78.18, others, .0)
        }
    }
}

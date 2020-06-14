package io.github.wulkanowy.sdk.scrapper.grades

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GradesStatisticsTest : BaseLocalTest() {

    private val snpPartial by lazy {
        runBlocking { getSnpRepo(GradesStatisticsTest::class.java, "Statystyki-czastkowe.html").getGradesStatistics(123, false) }
    }

    private val snpAnnual by lazy {
        runBlocking { getSnpRepo(GradesStatisticsTest::class.java, "Statystyki-roczne.html").getGradesStatistics(321, true) }
    }

    private val studentPartial by lazy {
        runBlocking { getStudentRepo(GradesStatisticsTest::class.java, "Statystyki-czastkowe.json").getGradesPartialStatistics(123) }
    }

    private val studentAnnual by lazy {
        runBlocking { getStudentRepo(GradesStatisticsTest::class.java, "Statystyki-roczne.json").getGradesAnnualStatistics(321) }
    }

    private val points by lazy {
        runBlocking { getStudentRepo(GradesStatisticsTest::class.java, "Statystyki-punkty.json").getGradesPointsStatistics(420) }
    }

    @Test
    fun getGradesStatistics() {
        assertEquals(12, snpPartial.size)
        assertEquals(12, studentPartial.size)
        assertEquals(6, snpAnnual.size)
        assertEquals(6, studentAnnual.size)
        assertEquals(3, points.size)
    }

    @Test
    fun getGradesStatistics_empty() {
        listOf(snpPartial[0], studentPartial[0]).map {
            it.run {
                assertEquals("Język polski", subject)
                assertEquals("6", grade)
                assertEquals(6, gradeValue)
                assertEquals(0, amount)
            }
        }
    }

    @Test
    fun getGradesStatistics_filled() {
        listOf(snpPartial[3], studentPartial[3]).map {
            it.run {
                assertEquals("Język polski", subject)
                assertEquals("3", grade)
                assertEquals(3, gradeValue)
                assertEquals(63, amount)
            }
        }
    }

    @Test
    fun getGradeStatistics_shortValue() {
        listOf(snpAnnual[1], studentAnnual[1]).map {
            it.run {
                assertEquals("Język angielski", subject)
                assertEquals("5", grade)
                assertEquals(5, gradeValue)
                assertEquals(4, amount)
            }
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

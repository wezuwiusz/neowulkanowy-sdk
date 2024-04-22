package io.github.wulkanowy.sdk.scrapper.grades

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GradesSummaryTest : BaseLocalTest() {

    private val grades by lazy {
        runBlocking { getStudentRepo(GradesTest::class.java, "Oceny.json").getGrades(0).summary }
    }

    private val points by lazy {
        runBlocking { getStudentRepo(GradesTest::class.java, "Oceny-points.json").getGrades(0).summary }
    }

    @Test
    fun getSummaryAverage_empty() {
        with(grades[0]) {
            assertEquals("Edukacja dla bezpieczeństwa", name)
            assertEquals(4.0, average, .0)
            assertEquals(3.0, averageAllYear)
            assertEquals("", predicted)
            assertEquals("", final)
        }
    }

    @Test
    fun getSummaryAverage_longFinal() {
        with(grades[1]) {
            assertEquals("Fizyka", name)
            assertEquals(.0, average, .0)
            assertEquals(null, averageAllYear)
            assertEquals("3", predicted)
            assertEquals("4", final)
        }
    }

    @Test
    fun getSummaryAverage_longPredictedAndFinal() {
        with(grades[2]) {
            assertEquals("Język angielski", name)
            assertEquals(.0, average, .0)
            assertEquals(null, averageAllYear)
            assertEquals("5", predicted)
            assertEquals("6", final)
        }
    }

    @Test
    fun getSummaryAverage_shortPredictedAndLongFinal() {
        with(grades[3]) {
            assertEquals("Język polski", name)
            assertEquals(.0, average, .0)
            assertEquals(null, averageAllYear)
            assertEquals("4/5", predicted)
            assertEquals("5", final)
        }
    }

    @Test
    fun getSummaryAverage_shortNegativePredictedAndFinal() {
        with(grades[4]) {
            assertEquals("Wychowanie fizyczne", name)
            assertEquals(.0, average, .0)
            assertEquals(null, averageAllYear)
            assertEquals("4-", predicted)
            assertEquals("5-", final)
        }
    }

    @Test
    fun getSummaryPoints_disabled() {
        grades.forEach {
            assertEquals("", it.pointsSum)
        }
    }

    @Test
    fun getSummaryPoints_enabled() {
        assertEquals("", points[0].pointsSum)
        assertEquals("123/200 (61%)", points[1].pointsSum)
    }
}

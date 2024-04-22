package io.github.wulkanowy.sdk.scrapper.grades

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GradesSummaryPlus : BaseLocalTest() {

    private val grades by lazy {
        runBlocking {
            getStudentPlusRepo(GradesTest::class.java, "OcenyPlus.json")
                .getGrades(0, 1, 2, 3)
                .summary
        }
    }

    @Test
    fun getAll() {
        assertEquals(3, grades.size)
    }

    @Test
    fun `get summary with empty average`() {
        with(grades[0]) {
            assertEquals("Zachowanie", name)
            assertEquals(.0, average, .0)
            assertEquals(null, averageAllYear)
            assertEquals("", predicted)
            assertEquals("", final)
        }
    }

    @Test
    fun `get summary with numeric average`() {
        with(grades[1]) {
            assertEquals("Zajęcia artystyczne", name)
            assertEquals(1.0, average, .0)
            assertEquals(2.0, averageAllYear)
            assertEquals("", predicted)
            assertEquals("", final)
        }
    }
}

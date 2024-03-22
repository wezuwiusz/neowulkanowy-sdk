package io.github.wulkanowy.sdk.scrapper.grades

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GradesPlusTest : BaseLocalTest() {

    private val grades by lazy {
        runBlocking {
            getStudentPlusRepo(GradesPlusTest::class.java, "OcenyPlus.json")
                .getGrades(1, 2, 3, 4)
                .details
        }
    }

    @Test
    fun getAllTest() {
        assertEquals(2, grades.size)
    }

    @Test
    fun getSimpleGrade() {
        with(grades[0]) {
            assertEquals("Zajęcia artystyczne", subject)
            assertEquals("6", entry)
            assertEquals(6, value)
            assertEquals("", comment)
            assertEquals(.0, modifier, .0)
            assertEquals("000000", colorHex)
            assertEquals("", symbol)
            assertEquals("", description)
            assertEquals("0,00", weight)
            assertEquals(0.0, weightValue, .0)
            assertEquals(getLocalDate(2024, 3, 19), date)
            assertEquals("Jan Kowalski [JK]", teacher)
        }
    }

    @Test
    fun getFullFeaturedGrade() {
        with(grades[1]) {
            assertEquals("Zajęcia artystyczne", subject)
            assertEquals("1", entry)
            assertEquals(1, value)
            assertEquals("bez możliwości poprawy", comment)
            assertEquals(.0, modifier, .0)
            assertEquals("F04C4C", colorHex)
            assertEquals("", symbol)
            assertEquals("Opis", description)
            assertEquals("100,00", weight)
            assertEquals(100.0, weightValue, .0)
            assertEquals(getLocalDate(2024, 3, 19), date)
            assertEquals("Joanna Kowalska [JK]", teacher)
        }
    }
}

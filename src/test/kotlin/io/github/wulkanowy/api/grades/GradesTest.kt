package io.github.wulkanowy.api.grades

import io.github.wulkanowy.api.BaseTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GradesTest : BaseTest() {

    private val grades by lazy {
        getSnpRepo(GradesTest::class.java, "OcenyWszystkie-filled.html").getGrades(0).blockingGet()
    }

    @Test fun getAllTest() {
        assertEquals(7, grades.size) // 2 items are skipped
    }

    @Test fun getSubjectTest() {
        assertEquals("Zajęcia z wychowawcą", grades[0].subject)
        assertEquals("Język angielski", grades[3].subject)
        assertEquals("Wychowanie fizyczne", grades[4].subject)
        assertEquals("Język polski", grades[5].subject)
    }

    @Test fun getValueTest() {
        assertEquals("5", grades[0].value)
        assertEquals("5", grades[3].value)
        assertEquals("1", grades[4].value)
        assertEquals("1", grades[5].value)
    }

    @Test fun getColorTest() {
        assertEquals("000000", grades[0].color)
        assertEquals("1289F7", grades[3].color)
        assertEquals("6ECD07", grades[4].color)
        assertEquals("6ECD07", grades[5].color)
    }

    @Test fun getSymbolTest() {
        assertEquals("A1", grades[0].symbol)
        assertEquals("BW3", grades[3].symbol)
        assertEquals("STR", grades[4].symbol)
        assertEquals("K", grades[5].symbol)
        assertEquals("+Odp", grades[6].symbol)
    }

    @Test fun getDescriptionTest() {
        assertEquals("Dzień Kobiet w naszej klasie", grades[0].description)
        assertEquals("Writing", grades[3].description)
        assertEquals("", grades[4].description)
        assertEquals("Kordian", grades[5].description)
        assertEquals("Kordian", grades[6].description)
    }

    @Test fun getWeightTest() {
        assertEquals("1,00", grades[0].weight)
        assertEquals("3,00", grades[3].weight)
        assertEquals("8,00", grades[4].weight)
        assertEquals("5,00", grades[5].weight)
    }

    @Test fun getDateTest() {
        assertEquals(getDate(2017, 3, 21), grades[0].date)
        assertEquals(getDate(2017, 6, 2), grades[3].date)
        assertEquals(getDate(2017, 4, 2), grades[4].date)
        assertEquals(getDate(2017, 2, 6), grades[5].date)
    }

    @Test fun getTeacherTest() {
        assertEquals("Patryk Maciejewski", grades[0].teacher)
        assertEquals("Oliwia Woźniak", grades[3].teacher)
        assertEquals("Klaudia Dziedzic", grades[4].teacher)
        assertEquals("Amelia Stępień", grades[5].teacher)
    }
}

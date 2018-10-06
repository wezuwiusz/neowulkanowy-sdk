package io.github.wulkanowy.api.grades

import io.github.wulkanowy.api.BaseTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GradesTest : BaseTest() {

    private val grades by lazy {
        getSnpRepo(GradesTest::class.java, "OcenyWszystkie-details.html").getGrades(0).blockingGet()
    }

    @Test
    fun getAllTest() {
        assertEquals(9, grades.size) // 2 items are skipped
    }

    @Test
    fun getGrade() {
        grades[0].run {
            assertEquals("Edukacja dla bezpieczeństwa", subject)
            assertEquals("4", entry)
            assertEquals(4, value)
            assertEquals(.0, modifier, .0)
            assertEquals("", comment)
            assertEquals("F04C4C", color)
            assertEquals("S1", symbol)
            assertEquals("PIERWSZA POMOC I RESUSCYTACJA", description)
            assertEquals("5,00", weight)
            assertEquals(5, weightValue)
            assertEquals(getDate(2017, 3, 31), date)
            assertEquals("Weronika Ratajczak", teacher)
        }
    }

    @Test
    fun getGrade_modifier() {
        grades[1].run {
            assertEquals("Fizyka", subject)
            assertEquals("2+", entry)
            assertEquals(2, value)
            assertEquals(.33, modifier, .0)
            assertEquals("", comment)
            assertEquals("6ECD07", color)
            assertEquals("O", symbol)
            assertEquals("Odpowiedź", description)
            assertEquals("3,00", weight)
            assertEquals(3, weightValue)
            assertEquals(getDate(2017, 6, 25), date)
            assertEquals("Jakub Michalak", teacher)
        }
    }

    @Test
    fun getGrade_modifierInverse() {
        grades[2].run {
            assertEquals("Język angielski", subject)
            assertEquals("-5", entry)
            assertEquals(5, value)
            assertEquals(-.33, modifier, .0)
            assertEquals("", comment)
            assertEquals("1289F7", color)
            assertEquals("BW3", symbol)
            assertEquals("Writing", description)
            assertEquals("3,00", weight)
            assertEquals(3, weightValue)
            assertEquals(getDate(2017, 6, 2), date)
            assertEquals("Oliwia Woźniak", teacher)
        }
    }

    @Test
    fun getGrade_shortDoubleModifier() {
        grades[3].run {
            assertEquals("Język polski", subject)
            assertEquals("2=", entry)
            assertEquals(2, value)
            assertEquals(-.50, modifier, .0)
            assertEquals("", comment)
            assertEquals("6ECD07", color)
            assertEquals("K", symbol)
            assertEquals("Kordian", description)
            assertEquals("5,00", weight)
            assertEquals(5, weightValue)
            assertEquals(getDate(2017, 2, 6), date)
            assertEquals("Amelia Stępień", teacher)
        }
    }

    @Test
    fun getGrade_noDescription() {
        grades[4].run {
            assertEquals("Wychowanie fizyczne", subject)
            assertEquals("1+", entry)
            assertEquals(1, value)
            assertEquals(.33, modifier, .0)
            assertEquals("", comment)
            assertEquals("6ECD07", color)
            assertEquals("STR", symbol)
            assertEquals("", description)
            assertEquals("8,00", weight)
            assertEquals(8, weightValue)
            assertEquals(getDate(2017, 4, 2), date)
            assertEquals("Klaudia Dziedzic", teacher)
        }
    }

    @Test
    fun getGrade_symbolSpecialChar() {
        grades[5].run {
            assertEquals("Język polski", subject)
            assertEquals("5+", entry)
            assertEquals(5, value)
            assertEquals(.33, modifier, .0)
            assertEquals("", comment)
            assertEquals("6ECD07", color)
            assertEquals("+Odp", symbol)
            assertEquals("Kordian", description)
            assertEquals("5,00", weight)
            assertEquals(5, weightValue)
            assertEquals(getDate(2017, 5, 11), date)
            assertEquals("Amelia Stępień", teacher)
        }
    }

    @Test
    fun getGrade_comment() {
        grades[6].run {
            assertEquals("Zajęcia z wychowawcą", subject)
            assertEquals("5", entry)
            assertEquals(5, value)
            assertEquals(.0, modifier, .0)
            assertEquals("pomoc przy tej uroczystości była wyjątkowa", comment)
            assertEquals("000000", color)
            assertEquals("A1", symbol)
            assertEquals("Dzień Kobiet w naszej klasie", description)
            assertEquals("1,00", weight)
            assertEquals(1, weightValue)
            assertEquals(getDate(2017, 3, 21), date)
            assertEquals("Patryk Maciejewski", teacher)
        }
    }

    @Test
    fun getGrade_noNumeric() {
        grades[7].run {
            assertEquals("Język angielski", subject)
            assertEquals("65", entry)
            assertEquals(0, value)
            assertEquals(.0, modifier, .0)
            assertEquals("%", comment)
            assertEquals("20A4F7", color)
            assertEquals("MP1", symbol)
            assertEquals("matura próbna", description)
            assertEquals("0,00", weight)
            assertEquals(0, weightValue)
            assertEquals(getDate(2017, 6, 11), date)
            assertEquals("Jadwiga Czerwieńska", teacher)
        }
    }

    @Test
    fun getGrade_noNumericWithWeight() {
        grades[8].run {
            assertEquals("Język angielski", subject)
            assertEquals("nb", entry)
            assertEquals(0, value)
            assertEquals(.0, modifier, .0)
            assertEquals("", comment)
            assertEquals("F04C4C", color)
            assertEquals("S2", symbol)
            assertEquals("słownictwo (człowiek) 4.10", description)
            assertEquals("10,00", weight)
            assertEquals(0, weightValue)
            assertEquals(getDate(2018, 10, 4), date)
            assertEquals("Jadwiga Czerwieńska", teacher)
        }
    }
}

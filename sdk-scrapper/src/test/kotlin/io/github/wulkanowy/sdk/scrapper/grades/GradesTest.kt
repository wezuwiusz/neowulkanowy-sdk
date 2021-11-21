package io.github.wulkanowy.sdk.scrapper.grades

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GradesTest : BaseLocalTest() {

    private val grades by lazy {
        runBlocking { getStudentRepo(GradesTest::class.java, "Oceny.json").getGradesDetails(0) }
    }

    private val points by lazy {
        runBlocking { getStudentRepo(GradesTest::class.java, "Oceny-points.json").getGradesDetails(0) }
    }

    @Test
    fun getAllTest() {
        assertEquals(14, grades.size)
        assertEquals(4, points.size)
    }

    @Test
    fun getGrade() {
        with(grades[0]) {
            assertEquals("Edukacja dla bezpieczeństwa", subject)
            assertEquals("4", entry)
            assertEquals(4, value)
            assertEquals(.0, modifier, .0)
            assertEquals("", comment)
            assertEquals("F04C4C", color)
            assertEquals("S1", symbol)
            assertEquals("PIERWSZA POMOC I RESUSCYTACJA", description)
            assertEquals("5,00", weight)
            assertEquals(5.0, weightValue, .0)
            assertEquals(getDate(2018, 12, 12), date)
            assertEquals("Weronika Ratajczak", teacher)
        }
    }

    @Test
    fun getGrade_modifier() {
        with(grades[1]) {
            assertEquals("Fizyka", subject)
            assertEquals("2+", entry)
            assertEquals(2, value)
            assertEquals(.33, modifier, .0)
            assertEquals("", comment)
            assertEquals("6ECD07", color)
            assertEquals("O", symbol)
            assertEquals("Odpowiedź", description)
            assertEquals("3,00", weight)
            assertEquals(3.0, weightValue, .0)
            assertEquals(getDate(2018, 12, 5), date)
            assertEquals("Jakub Michalak", teacher)
        }
    }

    @Test
    fun getGrade_modifierInverse() {
        with(grades[2]) {
            assertEquals("Język angielski", subject)
            assertEquals("-5", entry)
            assertEquals(5, value)
            assertEquals(-.33, modifier, .0)
            assertEquals("", comment)
            assertEquals("1289F7", color)
            assertEquals("BW3", symbol)
            assertEquals("Writing", description)
            assertEquals("3,00", weight)
            assertEquals(3.0, weightValue, .0)
            assertEquals(getDate(2018, 11, 28), date)
            assertEquals("Oliwia Woźniak", teacher)
        }
    }

    @Test
    fun getGrade_shortDoubleModifier() {
        with(grades[3]) {
            assertEquals("Język polski", subject)
            assertEquals("2=", entry)
            assertEquals(2, value)
            assertEquals(-.50, modifier, .0)
            assertEquals("", comment)
            assertEquals("6ECD07", color)
            assertEquals("K", symbol)
            assertEquals("Kordian", description)
            assertEquals("5,00", weight)
            assertEquals(5.0, weightValue, .0)
            assertEquals(getDate(2018, 11, 21), date)
            assertEquals("Amelia Stępień", teacher)
        }
    }

    @Test
    fun getGrade_noDescription() {
        with(grades[4]) {
            assertEquals("Wychowanie fizyczne", subject)
            assertEquals("1+", entry)
            assertEquals(1, value)
            assertEquals(.33, modifier, .0)
            assertEquals("", comment)
            assertEquals("6ECD07", color)
            assertEquals("STR", symbol)
            assertEquals("", description)
            assertEquals("8,00", weight)
            assertEquals(8.0, weightValue, .0)
            assertEquals(getDate(2018, 11, 14), date)
            assertEquals("Klaudia Dziedzic", teacher)
        }
    }

    @Test
    fun getGrade_symbolSpecialChar() {
        with(grades[5]) {
            assertEquals("Język polski", subject)
            assertEquals("5+", entry)
            assertEquals(5, value)
            assertEquals(.33, modifier, .0)
            assertEquals("", comment)
            assertEquals("6ECD07", color)
            assertEquals("+Odp", symbol)
            assertEquals("Kordian", description)
            assertEquals("5,00", weight)
            assertEquals(5.0, weightValue, .0)
            assertEquals(getDate(2018, 11, 7), date)
            assertEquals("Amelia Stępień", teacher)
        }
    }

    @Test
    fun getGrade_comment() {
        with(grades[6]) {
            assertEquals("Zajęcia z wychowawcą", subject)
            assertEquals("5", entry)
            assertEquals(5, value)
            assertEquals(.0, modifier, .0)
            assertEquals("pomoc przy tej uroczystości była wyjątkowa (naprawdę)", comment)
            assertEquals("000000", color)
            assertEquals("A1", symbol)
            assertEquals("Dzień Kobiet w naszej klasie", description)
            assertEquals("1,50", weight)
            assertEquals(1.50, weightValue, .0)
            assertEquals(getDate(2018, 10, 31), date)
            assertEquals("Patryk Maciejewski", teacher)
        }
    }

    @Test
    fun getGrade_noNumeric() {
        with(grades[7]) {
            assertEquals("Język angielski", subject)
            assertEquals("65", entry)
            assertEquals(0, value)
            assertEquals(.0, modifier, .0)
            assertEquals("%", comment)
            assertEquals("20A4F7", color)
            assertEquals("MP1", symbol)
            assertEquals("matura próbna", description)
            assertEquals("0,00", weight)
            assertEquals(0.0, weightValue, .0)
            assertEquals(getDate(2018, 10, 24), date)
            assertEquals("Jadwiga Czerwieńska", teacher)
        }
    }

    @Test
    fun getGrade_noNumericWithWeight() {
        with(grades[8]) {
            assertEquals("Język angielski", subject)
            assertEquals("nb", entry)
            assertEquals(0, value)
            assertEquals(.0, modifier, .0)
            assertEquals("", comment)
            assertEquals("F04C4C", color)
            assertEquals("S2", symbol)
            assertEquals("słownictwo (człowiek) 4.10", description)
            assertEquals("10,00", weight)
            assertEquals(0.0, weightValue, .0)
            assertEquals(getDate(2018, 10, 4), date)
            assertEquals("Jadwiga Czerwieńska", teacher)
        }
    }

    @Test
    fun getGrade_bracesWithoutSpace() {
        with(grades[9]) {
            assertEquals("Język angielski", subject)
            assertEquals("...", entry)
            assertEquals(0, value)
            assertEquals(.0, modifier, .0)
            assertEquals("dop(2.03)", comment)
            assertEquals("F04C4C", color)
            assertEquals("ZAL", symbol)
            assertEquals("", description)
            assertEquals("10,00", weight)
            assertEquals(0.0, weightValue, .0)
            assertEquals(getDate(2018, 10, 3), date)
            assertEquals("Jadwiga Czerwieńska", teacher)
        }
    }

    @Test
    fun getGrade_onlyCommentEntry() {
        with(grades[10]) {
            assertEquals("Fizyka", subject)
            assertEquals("BK,3", entry)
            assertEquals(0, value)
            assertEquals(.0, modifier, .0)
            assertEquals("", comment)
            assertEquals("000000", color)
            assertEquals("d1", symbol)
            assertEquals("Sprawozdanie - wyznaczanie przyspieczenia.", description)
            assertEquals("3,00", weight)
            assertEquals(0.0, weightValue, .0)
            assertEquals(getDate(2017, 11, 23), date)
            assertEquals("Anonimus Max", teacher)
        }
    }

    @Test
    fun getGrade_onlyGradeInCommentEntry() {
        with(grades[11]) {
            assertEquals("Fizyka", subject)
            assertEquals("(0)", entry)
            assertEquals(0, value)
            assertEquals(.0, modifier, .0)
            assertEquals("", comment)
            assertEquals("000000", color)
            assertEquals("spr", symbol)
            assertEquals("sprawdzian z tematów 11-15", description)
            assertEquals("4,00", weight)
            assertEquals(0.0, weightValue, .0)
            assertEquals(getDate(2017, 11, 22), date)
            assertEquals("Anonimus Max", teacher)
        }
    }

    @Test
    fun getGrade_pointsEntry() {
        with(points[0]) {
            assertEquals("Edukacja dla bezpieczeństwa", subject)
            assertEquals("85%", entry)
            assertEquals(0, value)
            assertEquals(.0, modifier, .0)
            assertEquals("85/100", comment)
            assertEquals("6ECD07", color)
            assertEquals("", symbol)
            assertEquals("diagnoza", description)
            assertEquals("0,00", weight)
            assertEquals(0.0, weightValue, .0)
            assertEquals(getDate(2019, 9, 10), date)
            assertEquals("Weronika Ratajczak", teacher)
        }
    }

    @Test
    fun getGrade_noRealPointsEntry() {
        with(points[1]) {
            assertEquals("Fizyka", subject)
            assertEquals("0/0", entry)
            assertEquals(0, value)
            assertEquals(.0, modifier, .0)
            assertEquals("komentarz", comment)
            assertEquals("000000", color)
            assertEquals("O", symbol)
            assertEquals("Odpowiedź", description)
            assertEquals("0,00", weight)
            assertEquals(0.0, weightValue, .0)
            assertEquals(getDate(2018, 12, 5), date)
            assertEquals("Jakub Michalak", teacher)
        }
    }

    @Test
    fun getGrade_invalidPointsEntry() {
        with(points[2]) {
            assertEquals("Fizyka", subject)
            assertEquals("1/0", entry)
            assertEquals(0, value)
            assertEquals(.0, modifier, .0)
            assertEquals("komentarz", comment)
            assertEquals("000000", color)
            assertEquals("O", symbol)
            assertEquals("Odpowiedź", description)
            assertEquals("0,00", weight)
            assertEquals(0.0, weightValue, .0)
            assertEquals(getDate(2018, 12, 4), date)
            assertEquals("Jakub Michalak", teacher)
        }
    }

    @Test
    fun getGrade_decimalCounter() {
        with(points[3]) {
            assertEquals("Fizyka", subject)
            assertEquals("95%", entry)
            assertEquals(0, value)
            assertEquals(.0, modifier, .0)
            assertEquals("47.5/50", comment)
            assertEquals("000000", color)
            assertEquals("K3", symbol)
            assertEquals("Liczebniki", description)
            assertEquals("1,00", weight)
            assertEquals(0.0, weightValue, .0)
            assertEquals(getDate(2018, 12, 3), date)
            assertEquals("Jakub Michalak", teacher)
        }
    }

    @Test
    fun getGrade_doublePlus() {
        with(grades[12]) {
            assertEquals("Wychowanie fizyczne", subject)
            assertEquals("0++", entry)
            assertEquals(0, value)
            assertEquals(.5, modifier, .0)
            assertEquals("", comment)
            assertEquals("000000", color)
            assertEquals("A1", symbol)
            assertEquals("Podnoszenie ciężarów", description)
            assertEquals("0,00", weight)
            assertEquals(.0, weightValue, .0)
            assertEquals(getDate(2017, 9, 1), date)
            assertEquals("Klaudia Dziedzic", teacher)
        }
    }

    @Test
    fun getGrade_nullableDate() {
        with(grades[13]) {
            assertEquals("Edukacja dla bezpieczeństwa", subject)
            assertEquals("4", entry)
            assertEquals(4, value)
            assertEquals(.0, modifier, .0)
            assertEquals("", comment)
            assertEquals("F04C4C", color)
            assertEquals("S1", symbol)
            assertEquals("Rodzaje broni chemicznych", description)
            assertEquals("5,00", weight)
            assertEquals(5.0, weightValue, .0)
            assertEquals(getDate(1970, 1, 1, 1, 0, 0), date)
            assertEquals("Weronika Ratajczak", teacher)
        }
    }
}

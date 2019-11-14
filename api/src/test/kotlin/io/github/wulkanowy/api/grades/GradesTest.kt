package io.github.wulkanowy.api.grades

import io.github.wulkanowy.api.BaseLocalTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GradesTest : BaseLocalTest() {

    private val snp by lazy {
        getSnpRepo(GradesTest::class.java, "OcenyWszystkie-details.html").getGrades(0).blockingGet()
    }

    private val student by lazy {
        getStudentRepo(GradesTest::class.java, "Oceny.json").getGrades(0).blockingGet()
    }

    private val studentPoints by lazy {
        getStudentRepo(GradesTest::class.java, "Oceny-points.json").getGrades(0).blockingGet()
    }

    @Test
    fun getAllTest() {
        assertEquals(10, snp.size) // 2 items are skipped
        assertEquals(10, student.size)
        assertEquals(3, studentPoints.size)
    }

    @Test
    fun getGrade() {
        listOf(snp[0], student[0]).map {
            it.run {
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
    }

    @Test
    fun getGrade_modifier() {
        listOf(snp[1], student[1]).map {
            it.run {
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
    }

    @Test
    fun getGrade_modifierInverse() {
        listOf(snp[2], student[2]).map {
            it.run {
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
    }

    @Test
    fun getGrade_shortDoubleModifier() {
        listOf(snp[3], student[3]).map {
            it.run {
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
    }

    @Test
    fun getGrade_noDescription() {
        listOf(snp[4], student[4]).map {
            it.run {
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
    }

    @Test
    fun getGrade_symbolSpecialChar() {
        listOf(snp[5], student[5]).map {
            it.run {
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
    }

    @Test
    fun getGrade_comment() {
        listOf(snp[6], student[6]).map {
            it.run {
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
    }

    @Test
    fun getGrade_noNumeric() {
        listOf(snp[7], student[7]).map {
            it.run {
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
    }

    @Test
    fun getGrade_noNumericWithWeight() {
        listOf(snp[8], student[8]).map {
            it.run {
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
    }

    @Test
    fun getGrade_bracesWithoutSpace() {
        listOf(snp[9], student[9]).map {
            it.run {
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
    }

    @Test
    fun getGrade_pointsEntry() {
        with(studentPoints[0]) {
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
        with(studentPoints[1]) {
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
        with(studentPoints[2]) {
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
}

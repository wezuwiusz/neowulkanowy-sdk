package io.github.wulkanowy.api.realized

import io.github.wulkanowy.api.BaseLocalTest
import org.junit.Assert.assertEquals
import org.junit.Test

class RealizedTest : BaseLocalTest() {

    private val snp by lazy {
        getSnpRepo(RealizedTest::class.java, "Zrealizowane.html").getRealized(
            getLocalDate(2018, 9, 17),
            getLocalDate(2018, 9, 18)
        ).blockingGet()
    }

    private val student by lazy {
        getStudentRepo(RealizedTest::class.java, "Zrealizowane.json").getRealized(
            getLocalDate(2018, 9, 17),
            getLocalDate(2018, 9, 18)
        ).blockingGet()
    }

    @Test
    fun getRealizedTest() {
        assertEquals(3, snp.size)
        assertEquals(3, student.size)
    }

    @Test
    fun getSimple() {
        listOf(snp[0], student[0]).map {
            it.run {
                assertEquals(1, number)
                assertEquals(getDate(2018, 9, 17), date)
                assertEquals("Język angielski", subject)
                assertEquals("Human - vocabulary practice", topic)
                assertEquals("Angielska Amerykanka", teacher)
                assertEquals("An", teacherSymbol)
                assertEquals("", absence)
            }
        }
    }

    @Test
    fun getSimple_mutliCommas() {
        listOf(snp[1], student[1]).map {
            it.run {
                assertEquals(2, number)
                assertEquals(getDate(2018, 9, 17), date)
                assertEquals("Historia i społeczeństwo", subject)
                assertEquals("Powstanie listopadowe, Napoleon, i inne przecinki", topic)
                assertEquals("Histeryczna Jadwiga", teacher)
                assertEquals("Hi", teacherSymbol)
                assertEquals("", absence)
            }
        }
    }

    @Test
    fun getLesson_absence() {
        listOf(snp[2], student[2]).map {
            it.run {
                assertEquals(4, number)
                assertEquals(getDate(2018, 9, 18), date)
                assertEquals("Język polski", subject)
                assertEquals("Inspiracje krajobrazem gór w poezji", topic)
                assertEquals("Polonistka Joanna", teacher)
                assertEquals("Po", teacherSymbol)
                assertEquals("Nieobecność nieusprawiedliwiona", absence)
            }
        }
    }
}

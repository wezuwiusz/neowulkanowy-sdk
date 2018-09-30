package io.github.wulkanowy.api.realized

import io.github.wulkanowy.api.BaseTest
import org.junit.Assert.assertEquals
import org.junit.Test

class RealizedTest : BaseTest() {

    private val realized by lazy {
        getSnpRepo(RealizedTest::class.java, "Zrealizowane.html").getRealized(getLocalDate(2018, 9, 24)).blockingGet()
    }

    @Test
    fun getRealizedTest() {
        assertEquals(3, realized.size)
    }

    @Test
    fun getSimple() {
        realized[0].run {
            assertEquals(1, number)
            assertEquals(getDate(2018, 9, 17), date)
            assertEquals("Język angielski", subject)
            assertEquals("Human - vocabulary practice", topic)
            assertEquals("Angielska Amerykanka", teacher)
            assertEquals("An", teacherSymbol)
            assertEquals("", absence)
        }
    }

    @Test
    fun getSimple_mutliCommas() {
        realized[1].run {
            assertEquals(2, number)
            assertEquals(getDate(2018, 9, 17), date)
            assertEquals("Historia i społeczeństwo", subject)
            assertEquals("Powstanie listopadowe, Napoleon, i inne przecinki", topic)
            assertEquals("Histeryczna Jadwiga", teacher)
            assertEquals("Hi", teacherSymbol)
            assertEquals("", absence)
        }
    }

    @Test
    fun getLesson_absence() {
        realized[2].run {
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

package io.github.wulkanowy.sdk.scrapper.timetable

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import io.github.wulkanowy.sdk.scrapper.exception.FeatureDisabledException
import io.github.wulkanowy.sdk.scrapper.exception.VulcanException
import io.github.wulkanowy.sdk.scrapper.register.RegisterTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CompletedLessonsTest : BaseLocalTest() {

    private val student by lazy {
        runBlocking {
            getStudentRepo(CompletedLessonsTest::class.java, "Zrealizowane.json").getCompletedLessons(
                start = getLocalDate(2018, 9, 17),
                endDate = getLocalDate(2018, 9, 18),
                subjectId = -1,
            )
        }
    }

    @Before
    fun setUp() {
        server.enqueue("UczenCache.json", RegisterTest::class.java)
    }

    @Test
    fun getRealizedTest() {
        // assertEquals(3, snp.size)
        assertEquals(3, student.size)
    }

    @Test
    fun getRealized_disabled() {
        try {
            runBlocking {
                getStudentRepo(CompletedLessonsTest::class.java, "Zrealizowane-disabled.json").getCompletedLessons(
                    getLocalDate(2018, 9, 17),
                    getLocalDate(2018, 9, 18),
                    -1,
                )
            }
        } catch (e: Throwable) {
            assertTrue(e is FeatureDisabledException)
            assertEquals("Widok lekcji zrealizowanych został wyłączony przez Administratora szkoły.", e.message)
        }
    }

    @Test
    fun getRealized_errored() {
        try {
            runBlocking {
                getStudentRepo(CompletedLessonsTest::class.java, "Zrealizowane-errored.json").getCompletedLessons(
                    getLocalDate(2018, 9, 17),
                    getLocalDate(2018, 9, 18),
                    -1,
                )
            }
        } catch (e: Throwable) {
            assertTrue(e is VulcanException)
            assertEquals("DB_ERROR", e.message)
        }
    }

    @Test
    fun getSimple() {
        with(student[0]) {
            assertEquals(1, number)
            assertEquals(getDate(2018, 9, 17), date)
            assertEquals("Język angielski", subject)
            assertEquals("Human - vocabulary practice", topic)
            assertEquals("Angielska Amerykanka", teacher)
            assertEquals("An", teacherSymbol)
            assertEquals("", substitution)
            assertEquals("", absence)
            assertEquals("", resources)
        }
    }

    @Test
    fun getSimple_mutliCommas() {
        with(student[1]) {
            assertEquals(2, number)
            assertEquals(getDate(2018, 9, 17), date)
            assertEquals("Historia i społeczeństwo", subject)
            assertEquals("Powstanie listopadowe, Napoleon, i inne przecinki", topic)
            assertEquals("Histeryczna Jadwiga", teacher)
            assertEquals("Hi", teacherSymbol)
            assertEquals("", substitution)
            assertEquals("", absence)
            assertEquals("", resources)
        }
    }

    @Test
    fun getLesson_absence() {
        with(student[2]) {
            assertEquals(4, number)
            assertEquals(getDate(2018, 9, 18), date)
            assertEquals("Język polski", subject)
            assertEquals("Inspiracje krajobrazem gór w poezji", topic)
            assertEquals("Polonistka Joanna", teacher)
            assertEquals("Po", teacherSymbol)
            assertEquals("", substitution)
            assertEquals("Nieobecność nieusprawiedliwiona", absence)
            assertEquals("", resources)
        }
    }
}

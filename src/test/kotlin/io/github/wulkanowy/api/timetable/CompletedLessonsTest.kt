package io.github.wulkanowy.api.timetable

import io.github.wulkanowy.api.BaseLocalTest
import io.github.wulkanowy.api.interceptor.FeatureDisabledException
import io.reactivex.observers.TestObserver
import org.junit.Assert.assertEquals
import org.junit.Test

class CompletedLessonsTest : BaseLocalTest() {

    private val snp by lazy {
        getSnpRepo(CompletedLessonsTest::class.java, "Zrealizowane.html").getCompletedLessons(
            getLocalDate(2018, 9, 17),
            getLocalDate(2018, 9, 18),
            -1
        ).blockingGet()
    }

    private val student by lazy {
        getStudentRepo(CompletedLessonsTest::class.java, "Zrealizowane.json").getCompletedLessons(
            getLocalDate(2018, 9, 17),
            getLocalDate(2018, 9, 18),
            -1
        ).blockingGet()
    }

    @Test
    fun getRealizedTest() {
        assertEquals(3, snp.size)
        assertEquals(3, student.size)
    }

    @Test
    fun getRealized_disabled() {
        val lessons = getStudentRepo(CompletedLessonsTest::class.java, "Zrealizowane-disabled.json").getCompletedLessons(
            getLocalDate(2018, 9, 17),
            getLocalDate(2018, 9, 18),
            -1
        )
        val lessonsObserver = TestObserver<List<CompletedLesson>>()
        lessons.subscribe(lessonsObserver)
        lessonsObserver.assertError(FeatureDisabledException::class.java)
        lessonsObserver.assertErrorMessage("Widok lekcji zrealizowanych został wyłączony przez Administratora szkoły.")
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
                assertEquals("", substitution)
                assertEquals("", absence)
                assertEquals("", resources)
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
                assertEquals("", substitution)
                assertEquals("", absence)
                assertEquals("", resources)
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
                assertEquals("", substitution)
                assertEquals("Nieobecność nieusprawiedliwiona", absence)
                assertEquals("", resources)
            }
        }
    }
}

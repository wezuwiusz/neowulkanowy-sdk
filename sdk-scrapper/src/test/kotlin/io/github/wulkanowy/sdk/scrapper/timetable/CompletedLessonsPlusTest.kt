package io.github.wulkanowy.sdk.scrapper.timetable

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import io.github.wulkanowy.sdk.scrapper.exception.FeatureDisabledException
import io.github.wulkanowy.sdk.scrapper.register.RegisterTest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class CompletedLessonsPlusTest : BaseLocalTest() {

    private val studentPlus by lazy {
        runBlocking {
            getStudentPlusRepo {
                it.enqueue("Context-all-enabled.json", RegisterTest::class.java)
                server.enqueue("Context-all-enabled.json", RegisterTest::class.java)
                server.enqueue("RealizacjaZajec.json", TimetableTest::class.java)
            }.getCompletedLessons(
                startDate = getLocalDate(2024, 1, 15),
                endDate = getLocalDate(2024, 1, 22),
                studentId = 1,
                diaryId = 2,
                unitId = 3,
            )
        }
    }

    @Test
    fun getRealizedTest() {
        assertEquals(1, studentPlus.size)
    }

    @Test(expected = FeatureDisabledException::class)
    fun getRealized_disabled() = runTest {
        getStudentPlusRepo {
            it.enqueue("Context-all-enabled.json", RegisterTest::class.java)
            it.enqueue("Context-all-disabled.json", RegisterTest::class.java)
        }
            .getCompletedLessons(
                startDate = getLocalDate(2024, 1, 15),
                endDate = getLocalDate(2024, 1, 21),
                studentId = 1,
                diaryId = 2,
                unitId = 3,
            )
    }

    @Test
    fun getRealized_success() = runTest {
        with(studentPlus[0]) {
            assertEquals(1, number)
            assertEquals(getDate(2024, 1, 18), date)
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

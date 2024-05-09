package io.github.wulkanowy.sdk.scrapper.homework

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import io.github.wulkanowy.sdk.scrapper.register.RegisterTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class HomeworkPlusTest : BaseLocalTest() {

    private val homework by lazy {
        runBlocking {
            getStudentPlusRepo {
                it.enqueue("Context-all-enabled.json", RegisterTest::class.java)
                it.enqueue("SprawdzianyZadaniaDomowe.json", HomeworkPlusTest::class.java)
                it.enqueue("ZadanieDomoweSzczegoly.json", HomeworkPlusTest::class.java)
            }.getHomework(
                startDate = getLocalDate(2024, 10, 18),
                endDate = getLocalDate(2024, 3, 24),
                studentId = 1,
                diaryId = 2,
                unitId = 3,
            )
        }
    }

    @Test
    fun getAllTest() {
        Assert.assertEquals(1, homework.size)
    }

    @Test
    fun getHomeworkTest() {
        with(homework[0]) {
            Assert.assertEquals(getDate(2024, 3, 19), date)
            Assert.assertEquals(getDate(2024, 3, 19), entryDate)
            Assert.assertEquals("Zajęcia artystyczne", subject)
            Assert.assertEquals("opis zadania", content)
            Assert.assertEquals("Jan Kowalski", teacher)
            Assert.assertEquals("", teacherSymbol)
        }
    }
}

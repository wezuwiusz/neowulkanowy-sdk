package io.github.wulkanowy.sdk.scrapper.exams

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import io.github.wulkanowy.sdk.scrapper.homework.HomeworkTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ExamsPlusTest : BaseLocalTest() {

    private val exams: List<Exam> by lazy {
        runBlocking {
            getStudentPlusRepo {
                it.enqueue("SprawdzianyZadaniaDomowe.json", HomeworkTest::class.java)
                it.enqueue("SprawdzianSzczegoly.json", ExamsPlusTest::class.java)
                it.enqueue("ZadanieDomoweSzczegoly.json", HomeworkTest::class.java)
            }
                .getExams(
                    startDate = getLocalDate(2024, 3, 18),
                    endDate = getLocalDate(2024, 3, 22),
                    studentId = 1,
                    diaryId = 2,
                    unitId = 3,
                )
        }
    }

    @Test
    fun getAllTest() {
        assertEquals(1, exams.size)
    }

    @Test
    fun getFullExam() {
        with(exams[0]) {
            assertEquals("Zajęcia artystyczne", subject)
            assertEquals("Kartkówka", typeName)
            assertEquals("To jest opis", description)
            assertEquals("Jan Kowalski", teacher)
            assertEquals("", teacherSymbol)
            assertEquals(getDate(2024, 3, 19), entryDate)
        }
    }
}

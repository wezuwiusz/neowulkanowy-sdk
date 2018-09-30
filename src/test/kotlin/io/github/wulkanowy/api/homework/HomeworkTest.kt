package io.github.wulkanowy.api.homework

import io.github.wulkanowy.api.BaseTest
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeworkTest : BaseTest() {

    private val full by lazy {
        getSnpRepo(HomeworkTest::class.java, "ZadaniaDomowe.html").getHomework(getLocalDate(2018, 10, 1)).blockingGet()
    }

    @Test fun getHomework() {
        assertEquals(2, full.size)
    }

    @Test fun getDate() {
        assertEquals(getDate(2018, 10, 1), full[0].date)
        assertEquals(getDate(2018, 10, 1), full[1].date)
    }

    @Test
    fun getEntryDate() {
        assertEquals(getDate(2018, 9, 25), full[0].entryDate)
        assertEquals(getDate(2018, 9, 16), full[1].entryDate)
    }

    @Test fun getSubject() {
        assertEquals("Naprawa komputera", full[0].subject)
        assertEquals("Sieci komputerowe i administrowanie sieciami", full[1].subject)
    }

    @Test fun getContent() {
        assertEquals("Test diagnozujący", full[0].content)
        assertEquals("Zadania egzaminacyjne", full[1].content)
    }

    @Test fun getTeacher() {
        assertEquals("Mickiewicz Adam", full[0].teacher)
        assertEquals("Słowacki Juliusz", full[1].teacher)
    }

    @Test fun getTeacherSymbol() {
        assertEquals("MA", full[0].teacherSymbol)
        assertEquals("SJ", full[1].teacherSymbol)
    }
}

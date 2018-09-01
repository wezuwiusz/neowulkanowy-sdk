package io.github.wulkanowy.api.homework

import io.github.wulkanowy.api.BaseTest
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeworkTest : BaseTest() {

    private val full by lazy {
        getSnpRepo(HomeworkTest::class.java, "ZadaniaDomowe.html").getHomework(getDate(2018, 6, 18)).blockingGet()
    }

    @Test fun getHomework() {
        assertEquals(2, full.size)
    }

    @Test fun getDate() {
        assertEquals(getDate(2017, 10, 23), full[0].date)
        assertEquals(getDate(2017, 10, 23), full[1].date)
    }

    @Test
    fun getEntryDate() {
        assertEquals(getDate(2017, 10, 16), full[0].entryDate)
        assertEquals(getDate(2017, 10, 25), full[1].entryDate)
    }

    @Test fun getSubject() {
        assertEquals("Sieci komputerowe i administrowanie sieciami", full[0].subject)
        assertEquals("Naprawa komputera", full[1].subject)
    }

    @Test fun getContent() {
        assertEquals("Zadania egzaminacyjne", full[0].content)
        assertEquals("Test diagnozujący", full[1].content)
    }

    @Test fun getTeacher() {
        assertEquals("Słowacki Juliusz", full[0].teacher)
        assertEquals("Mickiewicz Adam", full[1].teacher)
    }

    @Test fun getTeacherSymbol() {
        assertEquals("SJ", full[0].teacherSymbol)
        assertEquals("MA", full[1].teacherSymbol)
    }
}

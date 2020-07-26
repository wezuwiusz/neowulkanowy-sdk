package io.github.wulkanowy.sdk.scrapper.homework

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import io.github.wulkanowy.sdk.scrapper.Scrapper
import io.github.wulkanowy.sdk.scrapper.repository.StudentRepository
import io.github.wulkanowy.sdk.scrapper.service.StudentService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeworkTest : BaseLocalTest() {

    private val studentRaw by lazy {
        StudentRepository(getService(StudentService::class.java, server.url("/").toString(), false, true, true, Scrapper.LoginType.STANDARD))
    }

    private val homework by lazy {
        runBlocking { getStudentRepo(HomeworkTest::class.java, "Homework.json").getHomework(getLocalDate(2018, 10, 1)) }
    }

    @Test
    fun getHomeworkList() {
        assertEquals(2, homework.size)
    }

    @Test
    fun getHomework() {
        with(homework[0]) {
            assertEquals(getDate(2018, 10, 1), date)
            assertEquals(getDate(2018, 9, 25), entryDate)
            assertEquals("Naprawa komputera", subject)
            assertEquals("Test diagnozujący", content)
            assertEquals("Mickiewicz Adam", teacher)
            assertEquals("MA", teacherSymbol)
        }
    }

    @Test
    fun getHomeworkWithAttachments() {
        with(homework[1]) {
            assertEquals(getDate(2018, 10, 1), date)
            assertEquals(getDate(2018, 9, 25), entryDate)
            assertEquals("Techniki biurowe", subject)
            assertEquals("Zadania egzaminacyjne:\nstr 231 \nstr 254", content)
            assertEquals("Mateusz Kowal", teacher)
            assertEquals("MK", teacherSymbol)
            assertEquals(2, attachments.size)
            assertEquals("<a href=\"https://wulkanowy.github.io/\" target=\"_blank\">Strona główna Wulkanowego</a>", attachments[0])
            assertEquals("<a href=\"https://github.com/wulkanowy/wulkanowy/\" target=\"_blank\">Repozytorium kodu</a>", attachments[1])
            with(_attachments[0]) {
                assertEquals("https://wulkanowy.github.io/", first)
                assertEquals("Strona główna Wulkanowego", second)
            }
        }
    }

    @Test
    fun getNewHomeworkOnOldPage() {
        server.enqueue("HomeworkErrorNullParam.json")
        server.enqueue("ZadaniaDomowe.json")
        server.start(3030)

        val homework = runBlocking { studentRaw.getHomework(getLocalDate(2018, 10, 1)) }
        assertEquals(2, homework.size)
    }
}

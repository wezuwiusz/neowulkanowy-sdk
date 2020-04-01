package io.github.wulkanowy.sdk.scrapper.homework

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeworkTest : BaseLocalTest() {

    private val snp by lazy {
        getSnpRepo(HomeworkTest::class.java, "ZadaniaDomowe.html").getHomework(getLocalDate(2018, 10, 1)).blockingGet()
    }

    private val student by lazy {
        getStudentRepo(HomeworkTest::class.java, "ZadaniaDomowe.json").getHomework(getLocalDate(2018, 10, 1)).blockingGet()
    }

    @Test
    fun getHomeworkList() {
        assertEquals(2, snp.size)
        assertEquals(2, student.size)
    }

    @Test
    fun getHomework() {
        listOf(snp[0], student[0]).map {
            it.run {
                assertEquals(getDate(2018, 10, 1), date)
                assertEquals(getDate(2018, 9, 25), entryDate)
                assertEquals("Naprawa komputera", subject)
                assertEquals("Test diagnozujący", content)
                assertEquals("Mickiewicz Adam", teacher)
                assertEquals("MA", teacherSymbol)
            }
        }
    }

    @Test
    fun getHomeworkWithAttachments() {
            with(student[1]) {
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
}

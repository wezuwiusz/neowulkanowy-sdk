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
}

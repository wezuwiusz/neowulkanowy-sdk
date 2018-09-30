package io.github.wulkanowy.api.exams

import io.github.wulkanowy.api.BaseTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ExamsTest : BaseTest() {

    private val onePerDay by lazy {
        getSnpRepo(ExamsTest::class.java, "Sprawdziany-one-per-day.html").getExams(getLocalDate(2018, 10, 1)).blockingGet()
    }

    private val empty by lazy {
        getSnpRepo(ExamsTest::class.java, "Sprawdziany-empty.html").getExams(getLocalDate(2018, 10, 1)).blockingGet()
    }

    @Test
    fun getExamsSizeTest() {
        assertEquals(6, onePerDay.size)
        assertEquals(0, empty.size)
    }

    @Test fun getExamsDateTest() {
        assertEquals(getDate(2018, 10, 1), onePerDay[0].date)
        assertEquals(getDate(2018, 10, 2), onePerDay[1].date)
        assertEquals(getDate(2018, 10, 3), onePerDay[2].date)
        assertEquals(getDate(2018, 10, 3), onePerDay[3].date)
        assertEquals(getDate(2018, 10, 4), onePerDay[4].date)
        assertEquals(getDate(2018, 10, 5), onePerDay[5].date)
    }

    @Test fun getExamSubjectTest() {
        assertEquals("Sieci komputerowe", onePerDay[0].subject)
        assertEquals("Język angielski", onePerDay[1].subject)
        assertEquals("Język polski", onePerDay[4].subject)
        assertEquals("Metodologia programowania", onePerDay[5].subject)
    }

    @Test fun getExamGroupTest() {
        assertEquals("zaw2", onePerDay[0].group)
        assertEquals("J1", onePerDay[1].group)
        assertEquals("", onePerDay[4].group)
    }

    @Test fun getExamTypeTest() {
        assertEquals("Sprawdzian", onePerDay[0].type)
        assertEquals("Sprawdzian", onePerDay[1].type)
        assertEquals("Sprawdzian", onePerDay[2].type)
        assertEquals("Kartkówka", onePerDay[3].type)
    }

    @Test fun getExamDescriptionTest()  {
        assertEquals("Łącza danych", onePerDay[0].description)
        assertEquals("Czasy teraźniejsze", onePerDay[1].description)
        assertEquals("", onePerDay[5].description)
    }

    @Test fun getExamTeacherTest() {
        assertEquals("Adam Wiśniewski", onePerDay[0].teacher)
        assertEquals("Natalia Nowak", onePerDay[1].teacher)
        assertEquals("Małgorzata Nowacka", onePerDay[5].teacher)
    }

    @Test fun getExamTeacherSymbolTest() {
        assertEquals("AW", onePerDay[0].teacherSymbol)
        assertEquals("NN", onePerDay[1].teacherSymbol)
        assertEquals("MN", onePerDay[5].teacherSymbol)
    }

    @Test fun getExamEntryDateTest() {
        assertEquals(getDate(2018, 9, 15), onePerDay[0].entryDate)
        assertEquals(getDate(2018, 9, 17), onePerDay[1].entryDate)
        assertEquals(getDate(2018, 9, 16), onePerDay[5].entryDate)
    }
}

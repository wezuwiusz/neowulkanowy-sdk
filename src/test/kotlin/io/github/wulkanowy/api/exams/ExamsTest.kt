package io.github.wulkanowy.api.exams

import io.github.wulkanowy.api.BaseLocalTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ExamsTest : BaseLocalTest() {

    private val snp by lazy {
        getSnpRepo(ExamsTest::class.java, "Sprawdziany-one-per-day.html").getExams(getLocalDate(2018, 10, 1)).blockingGet()
    }

    private val snpEmpty by lazy {
        getSnpRepo(ExamsTest::class.java, "Sprawdziany-empty.html").getExams(getLocalDate(2018, 10, 1)).blockingGet()
    }

    private val student by lazy {
        getStudentRepo(ExamsTest::class.java, "Sprawdziany.json").getExams(getLocalDate(2018, 10, 1)).blockingGet()
    }

    @Test
    fun getExamsSizeTest() {
        assertEquals(6, snp.size)
        assertEquals(6, student.size)
        assertEquals(0, snpEmpty.size)
    }

    @Test
    fun getExam_normal() {
        listOf(snp[0], student[0]).map {
            it.run {
                assertEquals("Język polski", subject)
                assertEquals("", group)
                assertEquals("Sprawdzian", type)
                assertEquals("Dwudziestolecie", description)
                assertEquals("Czerwieńska Agata", teacher)
                assertEquals("CA", teacherSymbol)
                assertEquals(getDate(2018, 9, 16), entryDate)
            }
        }
    }

    @Test
    fun getExam_group() {
        listOf(snp[1], student[1]).map {
            it.run {
                assertEquals("Język angielski", subject)
                assertEquals("J1", group)
                assertEquals("Sprawdzian", type)
                assertEquals("Czasy teraźniejsze", description)
                assertEquals("Natalia Nowak", teacher)
                assertEquals("NN", teacherSymbol)
                assertEquals(getDate(2018, 9, 17), entryDate)
            }
        }
    }

    @Test
    fun getExam_type() {
        listOf(snp[2], student[2]).map {
            it.run {
                assertEquals("Metodologia programowania", subject)
                assertEquals("zaw1", group)
                assertEquals("Kartkówka", type)
                assertEquals("programowanie obiektowe", description)
                assertEquals("Małgorzata Nowacka", teacher)
                assertEquals("MN", teacherSymbol)
                assertEquals(getDate(2018, 9, 16), entryDate)
            }
        }
    }

    @Test
    fun getExam_emptyDescription() {
        listOf(snp[3], student[3]).map {
            it.run {
                assertEquals("Metodologia programowania", subject)
                assertEquals("zaw2", group)
                assertEquals("Sprawdzian", type)
                assertEquals("", description)
                assertEquals("Małgorzata Nowacka", teacher)
                assertEquals("MN", teacherSymbol)
                assertEquals(getDate(2018, 9, 16), entryDate)
            }
        }
    }
}

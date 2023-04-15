package io.github.wulkanowy.sdk.scrapper.exams

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ExamsTest : BaseLocalTest() {

    private val exams by lazy {
        runBlocking { getStudentRepo(ExamsTest::class.java, "Sprawdziany.json").getExams(getLocalDate(2018, 10, 1)) }
    }

    @Test
    fun getExamsSizeTest() {
        assertEquals(6, exams.size)
    }

    @Test
    fun getExam_normal() {
        with(exams[0]) {
            assertEquals("Język polski", subject)
            assertEquals("Sprawdzian", typeName)
            assertEquals("Dwudziestolecie", description)
            assertEquals("Czerwieńska Agata", teacher)
            assertEquals("CA", teacherSymbol)
            assertEquals(getDate(2018, 9, 16), entryDate)
        }
    }

    @Test
    fun getExam_group() {
        with(exams[1]) {
            assertEquals("Język angielski", subject)
            assertEquals("Sprawdzian", typeName)
            assertEquals("Czasy teraźniejsze", description)
            assertEquals("Natalia Nowak", teacher)
            assertEquals("NN", teacherSymbol)
            assertEquals(getDate(2018, 9, 17), entryDate)
        }
    }

    @Test
    fun getExam_type() {
        with(exams[2]) {
            assertEquals("Metodologia programowania", subject)
            assertEquals("Kartkówka", typeName)
            assertEquals("programowanie obiektowe", description)
            assertEquals("Małgorzata Nowacka", teacher)
            assertEquals("MN", teacherSymbol)
            assertEquals(getDate(2018, 9, 16), entryDate)
        }
    }

    @Test
    fun getExam_emptyDescription() {
        with(exams[3]) {
            assertEquals("Metodologia programowania", subject)
            assertEquals("Praca klasowa", typeName)
            assertEquals("", description)
            assertEquals("Małgorzata Nowacka", teacher)
            assertEquals("MN", teacherSymbol)
            assertEquals(getDate(2018, 9, 16), entryDate)
        }
    }
}

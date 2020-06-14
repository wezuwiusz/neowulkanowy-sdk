package io.github.wulkanowy.sdk.scrapper.school

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class TeachersTest : BaseLocalTest() {

    private val snp by lazy {
        runBlocking { getSnpRepo(TeachersTest::class.java, "Szkola.html").getTeachers() }
    }

    private val student by lazy {
        runBlocking { getStudentRepo(TeachersTest::class.java, "Szkola.json").getTeachers() }
    }

    @Test
    fun getTeachersSizeTest() {
        assertEquals(4, snp.size)
        assertEquals(4, student.size)
    }

    @Test
    fun getTeacher_std() {
        listOf(snp[3], student[3]).map {
            with(it) {
                assertEquals("Zbigniew Niedochodowicz", name)
                assertEquals("ZN", short)
                assertEquals("Zajęcia z wychowawcą", subject)
            }
        }
    }

    @Test
    fun getTeacher_stdSpliced() {
        listOf(snp[2], student[2]).map {
            with(it) {
                assertEquals("Karolina Kowalska", name)
                assertEquals("AN", short)
                assertEquals("Zajęcia z wychowawcą", subject)
            }
        }
    }

    @Test
    fun getTeacher_emptyTeacher() {
        listOf(snp[1], student[1]).map {
            with(it) {
                assertEquals("", name)
                assertEquals("", short)
                assertEquals("Podstawy przedsiębiorczości", subject)
            }
        }
    }

    @Test
    fun getTeacher_emptySubject() {
        listOf(snp[0], student[0]).map {
            with(it) {
                assertEquals("Zbigniew Niedochodowicz", name)
                assertEquals("ZN", short)
                assertEquals("", subject)
            }
        }
    }
}

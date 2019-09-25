package io.github.wulkanowy.api.school

import io.github.wulkanowy.api.BaseLocalTest
import org.junit.Assert.assertEquals
import org.junit.Test

class TeachersTest : BaseLocalTest() {

    private val snp by lazy {
        getSnpRepo(TeachersTest::class.java, "Szkola.html").getTeachers().blockingGet()
    }

    private val student by lazy {
        getStudentRepo(TeachersTest::class.java, "Szkola.json").getTeachers().blockingGet()
    }

    @Test
    fun getTeachersSizeTest() {
        assertEquals(3, snp.size)
        assertEquals(3, student.size)
    }

    @Test
    fun getTeacher_std() {
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
                assertEquals(null, name)
                assertEquals(null, short)
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
                assertEquals(null, subject)
            }
        }
    }
}

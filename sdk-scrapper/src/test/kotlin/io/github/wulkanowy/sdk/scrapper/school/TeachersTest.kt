package io.github.wulkanowy.sdk.scrapper.school

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class TeachersTest : BaseLocalTest() {

    private val teachers by lazy {
        runBlocking { getStudentRepo(TeachersTest::class.java, "Szkola.json").getTeachers() }
    }

    @Test
    fun getTeachersSizeTest() {
        assertEquals(4, teachers.size)
    }

    @Test
    fun getTeacher_std() {
        with(teachers[3]) {
            assertEquals("Zbigniew Niedochodowicz", name)
            assertEquals("ZN", short)
            assertEquals("Zajęcia z wychowawcą", subject)
        }
    }

    @Test
    fun getTeacher_stdSpliced() {
        with(teachers[2]) {
            assertEquals("Karolina Kowalska", name)
            assertEquals("AN", short)
            assertEquals("Zajęcia z wychowawcą", subject)
        }
    }

    @Test
    fun getTeacher_emptyTeacher() {
        with(teachers[1]) {
            assertEquals("", name)
            assertEquals("", short)
            assertEquals("Podstawy przedsiębiorczości", subject)
        }
    }

    @Test
    fun getTeacher_emptySubject() {
        with(teachers[0]) {
            assertEquals("Zbigniew Niedochodowicz", name)
            assertEquals("ZN", short)
            assertEquals("", subject)
        }
    }
}

package io.github.wulkanowy.api.school

import io.github.wulkanowy.api.BaseTest
import org.junit.Assert.assertEquals
import org.junit.Test

class TeachersTest : BaseTest() {

    private val teachers by lazy {
        getSnpRepo(TeachersTest::class.java, "Szkola.html").getTeachers().blockingGet()
    }

    @Test fun getTeachersSizeTest() {
        assertEquals(21, teachers.size)
    }

    @Test fun getSubjectTest() {
        assertEquals("Biologia", teachers[0].subject)
    }

    @Test fun getSubjectFromManyTeachersTest() {
        assertEquals("Język angielski", teachers[6].subject)
        assertEquals("Język angielski", teachers[7].subject)
    }

    @Test fun getTeacherNameTest() {
        assertEquals("Karolina Kowalska", teachers[0].name)
        assertEquals("Karolina Kowalska", teachers[7].name)
        assertEquals("Mateusz Kowal", teachers[8].name)
    }

    @Test fun getTeacherShortTest() {
        assertEquals("AN", teachers[0].short)
        assertEquals("AM", teachers[6].short)
        assertEquals("MK", teachers[8].short)
    }
}

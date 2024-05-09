package io.github.wulkanowy.sdk.scrapper.school

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import io.github.wulkanowy.sdk.scrapper.register.RegisterTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class TeachersPlusTest : BaseLocalTest() {
    private val teachers by lazy {
        runBlocking {
            getStudentPlusRepo {
                it.enqueue("Context-all-enabled.json", RegisterTest::class.java)
                it.enqueue("NauczycielePlus.json")
            }.getTeachers(1, 2, 3)
        }
    }

    @Test
    fun getTeachersSizeTest() {
        Assert.assertEquals(9, teachers.size)
    }

    @Test
    fun getTeacher_std() {
        with(teachers[3]) {
            Assert.assertEquals("Michał Bodnar", name)
            Assert.assertEquals("", short)
            Assert.assertEquals("Geografia", subject)
        }
    }

    @Test
    fun getTeacher_emptyTeacher() {
        with(teachers[1]) {
            Assert.assertEquals("", name)
            Assert.assertEquals("", short)
            Assert.assertEquals("Informatyka", subject)
        }
    }

    @Test
    fun getTeacher_emptySubject() {
        with(teachers[0]) {
            Assert.assertEquals("Joanna Budna", name)
            Assert.assertEquals("", short)
            Assert.assertEquals("", subject)
        }
    }
}

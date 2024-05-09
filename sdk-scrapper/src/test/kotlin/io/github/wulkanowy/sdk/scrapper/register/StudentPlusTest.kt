package io.github.wulkanowy.sdk.scrapper.register

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Ignore
import org.junit.Test

class StudentPlusTest : BaseLocalTest() {

    @Ignore("thanks to VULCAN")
    @Test(expected = NoSuchElementException::class)
    fun `get current student when there is no matching student`() = runTest {
        val repo = getStudentPlusRepo {
            it.enqueue("Context-all-enabled.json")
        }
        repo.getStudent(2)
    }

    @Test
    fun `get current student info without error but with empty real semesters`() = runTest {
        val repo = getStudentPlusRepo {
            it.enqueue("Context-all-enabled.json")
            it.enqueueContent("[]")
        }
        val student = repo.getStudent(1)
        with(student) {
            assertEquals(1, studentId)
            assertEquals("Jan", studentName)
            assertEquals("", studentSecondName)
            assertEquals("Kowalski", studentSurname)
            assertEquals("7a", className)
            assertEquals(0, classId)
            assertFalse(isParent)
            assertTrue(isAuthorized)

            assertEquals(1, semesters.size)
            assertEquals(-1, semesters[0].semesterId)
        }
    }

    @Test
    fun `get current student info without error and with semesters`() = runTest {
        val repo = getStudentPlusRepo {
            it.enqueue("Context-all-enabled.json")
            it.enqueue("OkresyKlasyfikacyjne.json", RegisterTest::class.java)
        }
        val student = repo.getStudent(1)
        with(student) {
            assertEquals(1, studentId)
            assertEquals("Jan", studentName)
            assertEquals("", studentSecondName)
            assertEquals("Kowalski", studentSurname)
            assertEquals("7a", className)
            assertEquals(0, classId)
            assertFalse(isParent)
            assertTrue(isAuthorized)

            assertEquals(2, semesters.size)
            assertEquals(12, semesters[0].semesterId)
            assertEquals(13, semesters[1].semesterId)
        }
    }
}

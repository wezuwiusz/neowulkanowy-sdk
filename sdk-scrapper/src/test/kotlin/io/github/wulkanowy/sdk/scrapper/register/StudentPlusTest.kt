package io.github.wulkanowy.sdk.scrapper.register

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StudentPlusTest : BaseLocalTest() {

    @Test
    fun `get current student info without error but with empty real semesters`() = runTest {
        val repo = getStudentPlusRepo {
            it.enqueue("Context-all-enabled.json")
            it.enqueueContent("[]")
        }
        val student = repo.getStudent(1)
        with(requireNotNull(student)) {
            assertEquals(1, studentId)
            assertEquals("Jan", studentName)
            assertEquals("", studentSecondName)
            assertEquals("Kowalski", studentSurname)
            assertEquals("7a", className)
            assertEquals(0, classId)
            assertFalse(isParent)
            assertEquals(1, semesters.size)
            assertTrue(isAuthorized)

            with(semesters[0]) {
                assertEquals(-1, semesterId)
            }
        }
    }

    @Test
    fun `get current student info without error and with semesters`() = runTest {
        val repo = getStudentPlusRepo {
            it.enqueue("Context-all-enabled.json")
            it.enqueue("OkresyKlasyfikacyjne.json", RegisterTest::class.java)
        }
        val student = repo.getStudent(1)
        with(requireNotNull(student)) {
            assertEquals(1, studentId)
            assertEquals("Jan", studentName)
            assertEquals("", studentSecondName)
            assertEquals("Kowalski", studentSurname)
            assertEquals("7a", className)
            assertEquals(0, classId)
            assertFalse(isParent)
            assertEquals(2, semesters.size)
            assertTrue(isAuthorized)

            assertEquals(12, semesters[0].semesterId)
            assertEquals(13, semesters[1].semesterId)
        }
    }
}

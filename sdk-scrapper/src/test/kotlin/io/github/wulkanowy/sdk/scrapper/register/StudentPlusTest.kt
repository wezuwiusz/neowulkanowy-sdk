package io.github.wulkanowy.sdk.scrapper.register

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StudentPlusTest : BaseLocalTest() {

    @Test
    fun `get current student info without error`() = runTest {
        val repo = getStudentPlusRepo {
            it.enqueue("Context-all-enabled.json")
        }
        val student = repo.getStudent(1, 2, 3)
        with(requireNotNull(student)) {
            assertEquals(1, studentId)
            assertEquals("Jan", studentName)
            assertEquals("", studentSecondName)
            assertEquals("Kowalski", studentSurname)
            assertEquals("7a", className)
            assertEquals(0, classId)
            assertFalse(isParent)
            assertEquals(emptyList<Semester>(), semesters)
            assertTrue(isAuthorized)
        }
    }
}

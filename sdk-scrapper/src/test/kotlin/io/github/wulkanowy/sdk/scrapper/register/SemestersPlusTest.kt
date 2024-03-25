package io.github.wulkanowy.sdk.scrapper.register

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SemestersPlusTest : BaseLocalTest() {

    @Test
    fun `get semesters when diary and unit id are not set`() = runTest {
        val repo = getStudentPlusRepo {
            // ~ ❯❯❯ echo "MS0yLTEtMw==" | base64 -d
            // 1-2-1-3
            it.enqueue("Context-all-disabled.json", RegisterTest::class.java)
            it.enqueue("OkresyKlasyfikacyjne.json", RegisterTest::class.java)
        }
        val semesters = repo.getSemesters(1, 0, 0)

        assertEquals(2, semesters.size)
    }

    @Test(expected = NoSuchElementException::class)
    fun `get semesters when there is no matching student list`() = runTest {
        val repo = getStudentPlusRepo {
            // ~ ❯❯❯ echo "MS0yLTEtMw==" | base64 -d
            // 1-2-1-3
            it.enqueue("Context-all-disabled.json", RegisterTest::class.java)
        }
        repo.getSemesters(2, 0, 0)
    }

    @Test
    fun `get semesters when there is empty semesters list`() = runTest {
        val repo = getStudentPlusRepo {
            it.enqueue("Context-all-disabled.json", RegisterTest::class.java)
            it.enqueue("[]")
        }
        val semesters = repo.getSemesters(1, 0, 0)

        assertEquals(0, semesters.size)
    }
}

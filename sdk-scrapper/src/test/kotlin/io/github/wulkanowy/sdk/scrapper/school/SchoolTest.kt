package io.github.wulkanowy.sdk.scrapper.school

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class SchoolTest : BaseLocalTest() {

    private val school by lazy {
        runBlocking { getStudentRepo(TeachersTest::class.java, "Szkola.json").getSchool() }
    }

    @Test
    fun getSchool() {
        with(school) {
            assertEquals("Publiczna szkoła Wulkanowego nr 1 w fakelog.cf", name)
            assertEquals("", address)
            assertEquals("", contact)
            assertEquals("Karolina Kowalska", headmaster)
            assertEquals("Stanisław Krupa", pedagogue)
        }
    }
}

package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.mobile.BaseLocalTest
import io.github.wulkanowy.sdk.mobile.register.RegisterTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.threeten.bp.LocalDate.of

class SemesterMapperTest : BaseLocalTest() {

    private val mobile by lazy {
        Sdk().apply {
            mode = Sdk.Mode.API
            mobileBaseUrl = server.url("/").toString()
        }
    }

    @Test
    fun getStudents_api2() {
        server.enqueueAndStart("ListaUczniow.json", RegisterTest::class.java)

        mobile.studentId = 1
        val semesters = runBlocking { mobile.getSemesters() }
        assertEquals(2, semesters.size)

        with(semesters[0]) {
            assertEquals(2, semesterNumber)
            assertEquals(of(2020, 1, 30), start)
        }
        with(semesters[1]) {
            assertEquals(1, semesterNumber)
            assertEquals(of(2019, 9, 1), start)
        }
    }

    @Test
    fun getStudents_api1() {
        server.enqueueAndStart("ListaUczniow-2.json", RegisterTest::class.java)

        mobile.studentId = 1
        val semesters = runBlocking { mobile.getSemesters() }
        assertEquals(2, semesters.size)

        with(semesters[0]) {
            assertEquals(1, semesterNumber)
            assertEquals(of(2019, 9, 1), start)
        }
        with(semesters[1]) {
            assertEquals(2, semesterNumber)
            assertEquals(of(2020, 1, 30), start)
        }
    }
}

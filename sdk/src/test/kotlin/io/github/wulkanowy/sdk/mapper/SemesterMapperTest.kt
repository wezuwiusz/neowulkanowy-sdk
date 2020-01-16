package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.mobile.BaseLocalTest
import io.github.wulkanowy.sdk.mobile.register.RegisterTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SemesterMapperTest : BaseLocalTest() {

    private val mobile by lazy {
        Sdk().apply {
            mode = Sdk.Mode.API
            mobileBaseUrl = server.url("/").toString()
        }
    }

    @Test
    fun getStudents_api() {
        server.enqueueAndStart("ListaUczniow.json", RegisterTest::class.java) // TODO: modify response to match current now()

        mobile.studentId = 1
        val semesters = mobile.getSemesters().blockingGet()
        assertEquals(2, semesters.size)

        with(semesters[0]) {
            assertEquals(2, semesterNumber)
            assertEquals(false, current)
        }
        with(semesters[1]) {
            assertEquals(1, semesterNumber)
            assertEquals(true, current)
        }
    }
}

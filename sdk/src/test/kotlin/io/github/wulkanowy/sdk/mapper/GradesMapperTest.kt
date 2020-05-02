package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.mobile.BaseLocalTest
import io.github.wulkanowy.sdk.mobile.grades.GradesTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GradesMapperTest : BaseLocalTest() {

    private val mobile by lazy {
        Sdk().apply {
            mode = Sdk.Mode.API
            mobileBaseUrl = server.url("/").toString()
        }
    }

    @Test
    fun getApiGrades() {
        server.enqueue("Slowniki.json", BaseLocalTest::class.java)
        server.enqueueAndStart("Oceny.json", GradesTest::class.java)

        val grades = mobile.getGradesDetails(0).blockingGet()
        assertEquals(2, grades.size)

        with(grades[0]) {
            assertEquals("3", entry)
            assertEquals("Akt", symbol)
            assertEquals("Aktywność", description)
        }
    }
}

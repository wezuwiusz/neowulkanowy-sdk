package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.mobile.BaseLocalTest
import io.github.wulkanowy.sdk.mobile.grades.GradesTest
import kotlinx.coroutines.runBlocking
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
        server.enqueueAndStart("Oceny.json", GradesTest::class.java)
        server.enqueue("Slowniki.json", BaseLocalTest::class.java)

        val grades = runBlocking { mobile.getGradesDetails(0) }
        assertEquals(2, grades.size)

        with(grades[0]) {
            assertEquals("3", entry)
            assertEquals("Akt", symbol)
            assertEquals("Aktywność", description)
        }
    }
}

package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.mobile.BaseLocalTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SubjectsMapperTest : BaseLocalTest() {

    private val mobile by lazy {
        Sdk().apply {
            mode = Sdk.Mode.API
            mobileBaseUrl = server.url("/").toString()
        }
    }

    @Test
    fun getApiSubjects() {
        server.enqueueAndStart("Slowniki.json", BaseLocalTest::class.java)

        val subjects = mobile.getSubjects().blockingGet()
        assertEquals(15, subjects.size)
        with(subjects[0]) {
            assertEquals(-1, id)
            assertEquals("Wszystkie", name)
        }
    }
}

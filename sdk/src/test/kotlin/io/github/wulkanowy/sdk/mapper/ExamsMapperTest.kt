package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.mobile.BaseLocalTest
import io.github.wulkanowy.sdk.mobile.exams.ExamsTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.threeten.bp.LocalDate.now

class ExamsMapperTest : BaseLocalTest() {

    private val mobile by lazy {
        Sdk().apply {
            mode = Sdk.Mode.API
            mobileBaseUrl = server.url("/").toString()
        }
    }

    @Test
    fun getApiExams() {
        server.enqueueAndStart("Sprawdziany.json", ExamsTest::class.java)
        server.enqueue("Slowniki.json", BaseLocalTest::class.java)

        val exams = mobile.getExams(now(), now(), 1).blockingGet()
        assertEquals(3, exams.size)

        assertEquals("Sprawdzian", exams[0].type)
        assertEquals("Kartkówka", exams[1].type)
        assertEquals("Praca klasowa", exams[2].type)
    }
}

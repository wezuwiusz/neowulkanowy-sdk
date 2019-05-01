package io.github.wulkanowy.sdk

import org.junit.Assert.assertEquals
import org.junit.Test

class SdkRemoteTest {

    @Test
    fun getStudents_api() {
        val sdk = Sdk().apply {
            apiKey = "012345678901234567890123456789AB"

            mode = Sdk.Mode.API
            symbol = "Default"

            token = "FK100000"
            pin = "999999"
        }

        val students = sdk.getStudents().blockingGet()
        assertEquals(2, students.size)
    }

    @Test
    fun getStudents_scrapper() {
        val sdk = Sdk().apply {
            mode = Sdk.Mode.SCRAPPER
            symbol = "Default"

            ssl = false
            scrapperHost = "fakelog.cf"
            email = "jan@fakelog.cf"
            password = "jan123"
        }

        val students = sdk.getStudents().blockingGet()
        assertEquals(6, students.size)
    }

    @Test
    fun getStudents_hybrid() {
        val sdk = Sdk().apply {
            apiKey = "012345678901234567890123456789AB"

            mode = Sdk.Mode.HYBRID
            symbol = "Default"

            ssl = false
            scrapperHost = "fakelog.cf"
            email = "jan@fakelog.cf"
            password = "jan123"
        }

        val students = sdk.getStudents().blockingGet()
        assertEquals(12, students.size)
    }
}

package io.github.wulkanowy.sdk.scrapper.conferences

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ConferencesPlusTest : BaseLocalTest() {

    private val conferences by lazy {
        runBlocking {
            getStudentPlusRepo(ConferencesPlusTest::class.java, "ZebraniaPlus.json").getConferences(1, 2, 3)
        }
    }

    @Test
    fun getAllConferencesTest() {
        assertEquals(2, conferences.size)
    }

    @Test
    fun getSimpleConference() {
        with(conferences[0]) {
            assertEquals("Miejsce", place)
            assertEquals("Temat", topic)
            assertEquals("", agenda)
            assertEquals("", presentOnConference)
            assertEquals(2, id)
            assertEquals(getLocalDateTime(2024, 3, 20, 23, 5, 0), date)
        }
    }

    @Test
    fun getFullConference() {
        with(conferences[1]) {
            assertEquals("", place)
            assertEquals("Temat", topic)
            assertEquals("", agenda)
            assertEquals("Jan Kowalski", presentOnConference)
            assertEquals(1, id)
            assertEquals(getLocalDateTime(2024, 3, 19, 23, 5, 0), date)
        }
    }
}

package io.github.wulkanowy.sdk.scrapper.student

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ContextStudentInfoPlusTest : BaseLocalTest() {

    private val info by lazy {
        runBlocking {
            getStudentPlusRepo(ContextStudentInfoPlusTest::class.java, "DaneUcznia.json")
                .getStudentInfo(1, 2, 3)
        }
    }

    @Test
    fun getStudentInfoTest() {
        with(info) {
            assertEquals("Jan", name)
        }
    }
}

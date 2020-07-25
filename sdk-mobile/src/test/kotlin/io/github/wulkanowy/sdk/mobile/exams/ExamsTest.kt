package io.github.wulkanowy.sdk.mobile.exams

import io.github.wulkanowy.sdk.mobile.BaseLocalTest
import io.github.wulkanowy.sdk.mobile.repository.MobileRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.create
import java.time.LocalDate.of

class ExamsTest : BaseLocalTest() {

    private val exams by lazy { MobileRepository(getRetrofit().create()) }

    @Test
    fun getExams() {
        server.enqueueAndStart("Sprawdziany.json")

        val items = runBlocking { exams.getExams(of(2020, 1, 16), of(2020, 1, 17), 1, 2, 3) }

        assertEquals(3, items.size)
    }
}

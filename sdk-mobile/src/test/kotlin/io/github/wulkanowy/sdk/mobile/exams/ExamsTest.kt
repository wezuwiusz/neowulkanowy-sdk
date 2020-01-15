package io.github.wulkanowy.sdk.mobile.exams

import io.github.wulkanowy.sdk.mobile.BaseLocalTest
import io.github.wulkanowy.sdk.mobile.repository.MobileRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import org.threeten.bp.LocalDate.of
import retrofit2.create

class ExamsTest : BaseLocalTest() {

    private val exams by lazy { MobileRepository(getRetrofit().create()) }

    @Test
    fun getExams() {
        server.enqueueAndStart("Sprawdziany.json")

        val items = exams.getExams(of(2020, 1, 16), of(2020, 1, 17), 1, 2, 3).blockingGet()

        assertEquals(1, items.size)
    }
}

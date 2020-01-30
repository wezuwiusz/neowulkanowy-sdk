package io.github.wulkanowy.sdk.mobile.grades

import io.github.wulkanowy.sdk.mobile.BaseLocalTest
import io.github.wulkanowy.sdk.mobile.repository.MobileRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.create

class GradesTest : BaseLocalTest() {

    private val grades by lazy { MobileRepository(getRetrofit().create()) }

    @Test
    fun getGrades() {
        server.enqueueAndStart("Oceny.json")

        val items = grades.getGrades(0, 0, 0).blockingGet()

        assertEquals(2, items.size)
    }
}

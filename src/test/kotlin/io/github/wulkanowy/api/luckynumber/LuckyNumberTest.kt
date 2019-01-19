package io.github.wulkanowy.api.luckynumber

import io.github.wulkanowy.api.BaseLocalTest
import io.github.wulkanowy.api.repository.HomepageRepository
import io.github.wulkanowy.api.service.HomepageService
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Test

class LuckyNumberTest : BaseLocalTest() {

    private val api by lazy {
        HomepageRepository(getService(HomepageService::class.java, "http://fakelog.localhost:3000/", true))
    }

    @Test
    fun getLuckyNumber() {
        server.enqueue(MockResponse().setBody(LuckyNumberTest::class.java.getResource("Index.html").readText()))
        server.start(3000)

        assertEquals(18, api.getLuckyNumber().blockingGet())
    }

    @Test
    fun getLuckyNumber_empty() {
        server.enqueue(MockResponse().setBody(LuckyNumberTest::class.java.getResource("Index-withoutLuckyNumber.html").readText()))
        server.start(3000)

        assertEquals(null, api.getLuckyNumber().blockingGet())
    }

}

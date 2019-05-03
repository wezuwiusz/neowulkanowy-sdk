package io.github.wulkanowy.sdk.repository

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

class RegisterRepositoryTest {

    private val server = MockWebServer()

    @Test
    fun getRouteByToken() {
        server.enqueue(MockResponse().setBody(RegisterRepositoryTest::class.java.getResource("RoutingRules.txt").readText()))
        server.start(3000)

        val repo = RegisterRepository("")
        repo.baseHost = "http://localhost:3000"
        val route = repo.getRouteByToken("KA200000").blockingGet()

        assertEquals("https://uonetplus-komunikacja-test.mcuw.katowice.eu", route)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }
}

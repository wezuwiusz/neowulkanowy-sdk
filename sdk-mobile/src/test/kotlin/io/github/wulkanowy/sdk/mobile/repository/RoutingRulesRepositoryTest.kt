package io.github.wulkanowy.sdk.mobile.repository

import io.github.wulkanowy.sdk.mobile.BaseLocalTest
import io.github.wulkanowy.sdk.mobile.exception.InvalidTokenException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.create

class RoutingRulesRepositoryTest : BaseLocalTest() {

    @Test
    fun getRouteByToken() {
        server.enqueueAndStart("RoutingRules.txt")

        val repo = RoutingRulesRepository(getRetrofit().create())
        val route = runBlocking { repo.getRouteByToken("KA2000") }

        assertEquals("https://uonetplus-komunikacja-test.mcuw.katowice.eu", route)
    }

    @Test
    fun getRouteByToken_invalid() {
        server.enqueueAndStart("RoutingRules.txt")

        val repo = RoutingRulesRepository(getRetrofit().create())

        try {
            runBlocking { repo.getRouteByToken("ERR00000") }
        } catch (e: Throwable) {
            assertTrue(e is InvalidTokenException)
        }
    }

    @Test
    fun getRouteByToken_tooShort() {
        server.enqueueAndStart("RoutingRules.txt")

        val repo = RoutingRulesRepository(getRetrofit().create())

        // TODO: fix assert to run event if no exception thrown
        try {
            runBlocking { repo.getRouteByToken("ER") }
        } catch (e: Throwable) {
            assertTrue(e is InvalidTokenException)
        }
    }
}

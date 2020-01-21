package io.github.wulkanowy.sdk.mobile.repository

import io.github.wulkanowy.sdk.mobile.BaseLocalTest
import io.github.wulkanowy.sdk.mobile.exception.InvalidTokenException
import io.reactivex.observers.TestObserver
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.create

class RoutingRulesRepositoryTest : BaseLocalTest() {

    @Test
    fun getRouteByToken() {
        server.enqueueAndStart("RoutingRules.txt")

        val repo = RoutingRulesRepository(getRetrofit().create())
        val route = repo.getRouteByToken("KA2000").blockingGet()

        assertEquals("https://uonetplus-komunikacja-test.mcuw.katowice.eu", route)
    }

    @Test
    fun getRouteByToken_invalid() {
        server.enqueueAndStart("RoutingRules.txt")

        val repo = RoutingRulesRepository(getRetrofit().create())
        val route = repo.getRouteByToken("ERR00000")
        val routeObserver = TestObserver<String>()
        route.subscribe(routeObserver)
        routeObserver.assertNotComplete()
        routeObserver.assertError(InvalidTokenException::class.java)
    }

    @Test
    fun getRouteByToken_tooShort() {
        server.enqueueAndStart("RoutingRules.txt")

        val repo = RoutingRulesRepository(getRetrofit().create())
        val route = repo.getRouteByToken("ER")
        val routeObserver = TestObserver<String>()
        route.subscribe(routeObserver)
        routeObserver.assertNotComplete()
        routeObserver.assertError(InvalidTokenException::class.java)
    }
}

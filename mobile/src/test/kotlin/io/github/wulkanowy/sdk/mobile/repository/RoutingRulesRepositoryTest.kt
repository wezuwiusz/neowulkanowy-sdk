package io.github.wulkanowy.sdk.mobile.repository

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create

class RoutingRulesRepositoryTest {

    private val server = MockWebServer()

    @Test
    fun getRouteByToken() {
        server.enqueue(MockResponse().setBody(RoutingRulesRepositoryTest::class.java.getResource("RoutingRules.txt").readText()))
        server.start(3030)

        val repo = RoutingRulesRepository(getRetrofitBuilder().baseUrl("http://localhost:3030").build().create())
        val route = repo.getRouteByToken("KA200000").blockingGet()

        assertEquals("https://uonetplus-komunikacja-test.mcuw.katowice.eu", route)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    private fun getRetrofitBuilder(): Retrofit.Builder {
        return Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient().newBuilder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
            )
    }
}

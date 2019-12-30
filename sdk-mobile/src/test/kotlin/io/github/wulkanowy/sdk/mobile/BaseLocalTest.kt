package io.github.wulkanowy.sdk.mobile

import io.github.wulkanowy.sdk.mobile.interceptor.ErrorInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

open class BaseLocalTest {

    val server = MockWebServer()

    private fun MockWebServer.enqueue(fileName: String) {
        enqueue(MockResponse().setBody(this@BaseLocalTest::class.java.getResource(fileName).readText()))
    }

    fun MockWebServer.enqueueAndStart(fileName: String, port: Int = 3030) {
        enqueue(fileName)
        start(port)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    fun getRetrofitBuilder(): Retrofit.Builder {
        return Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient().newBuilder()
                .addInterceptor(ErrorInterceptor())
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
            )
    }
}

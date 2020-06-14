package io.github.wulkanowy.sdk.mobile

import io.github.wulkanowy.sdk.mobile.interceptor.ErrorInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

open class BaseLocalTest {

    val server = MockWebServer()

    fun MockWebServer.enqueue(fileName: String, clazz: Class<*>) {
        enqueue(MockResponse().setBody(clazz.getResource(fileName).readText()))
    }

    fun MockWebServer.enqueueAndStart(fileName: String, clazz: Class<*> = this@BaseLocalTest.javaClass, port: Int = 3030) {
        enqueue(fileName, clazz)
        start(port)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    fun getRetrofit(baseUrl: String = server.url("/").toString()): Retrofit = getRetrofitBuilder().baseUrl(baseUrl).build()

    fun getRetrofitBuilder(): Retrofit.Builder = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient().newBuilder()
            .addInterceptor(ErrorInterceptor())
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
            .build()
        )
}

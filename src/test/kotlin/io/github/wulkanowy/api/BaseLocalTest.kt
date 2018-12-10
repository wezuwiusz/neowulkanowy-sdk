package io.github.wulkanowy.api

import io.github.wulkanowy.api.interceptor.ErrorInterceptor
import io.github.wulkanowy.api.interceptor.NotLoggedInErrorInterceptor
import io.github.wulkanowy.api.repository.StudentAndParentRepository
import io.github.wulkanowy.api.service.StudentAndParentService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import pl.droidsonroids.retrofit2.JspoonConverterFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

abstract class BaseLocalTest : BaseTest() {

    val server = MockWebServer()

    @After
    fun tearDown() {
        server.shutdown()
    }

    fun getSnpRepo(testClass: Class<*>, fixture: String): StudentAndParentRepository {
        server.enqueue(MockResponse().setBody(testClass.getResource(fixture).readText()))
        return StudentAndParentRepository(getService(StudentAndParentService::class.java))
    }

    fun <T> getService(service: Class<T>, url: String = this.server.url("/").toString(), html: Boolean = true, errorInterceptor: Boolean = true, noLoggedInInterceptor: Boolean = true): T {
        return Retrofit.Builder()
                .client(OkHttpClient.Builder()
                        .apply {
                            if (errorInterceptor) addInterceptor(ErrorInterceptor())
                            if (noLoggedInInterceptor) addInterceptor(NotLoggedInErrorInterceptor())
                        }
                        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                        .build()
                )
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(if (html) JspoonConverterFactory.create() else GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(url)
                .build()
                .create(service)
    }
}

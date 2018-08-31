package io.github.wulkanowy.api

import io.github.wulkanowy.api.interceptor.ErrorInterceptor
import io.github.wulkanowy.api.repository.StudentAndParentRepository
import io.github.wulkanowy.api.service.StudentAndParentService
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Rule
import pl.droidsonroids.retrofit2.JspoonConverterFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

open class BaseTest {

    @JvmField @Rule
    val mockBackend = MockWebServer()

    fun getSnpRepo(testClass: Class<*>, fixture: String): StudentAndParentRepository {
        mockBackend.enqueue(MockResponse().setBody(testClass.getResource(fixture).readText()))
        return StudentAndParentRepository(getService(StudentAndParentService::class.java))
    }

    fun <T> getService(service: Class<T>, url: String = mockBackend.url("/").toString(), html: Boolean = true): T {
        return Retrofit.Builder()
                .client(OkHttpClient.Builder().addInterceptor(ErrorInterceptor()).build())
                .addConverterFactory(if (html) JspoonConverterFactory.create() else GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(url)
                .build()
                .create(service)
    }

    fun getDate(year: Int, month: Int, day: Int): Date {
        return Date.from(LocalDate.of(year, month, day)
                .atStartOfDay(ZoneId.systemDefault()).toInstant())
    }
}

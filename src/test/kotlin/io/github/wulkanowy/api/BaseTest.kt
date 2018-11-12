package io.github.wulkanowy.api

import io.github.wulkanowy.api.interceptor.ErrorInterceptor
import io.github.wulkanowy.api.repository.StudentAndParentRepository
import io.github.wulkanowy.api.service.StudentAndParentService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Rule
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import pl.droidsonroids.retrofit2.JspoonConverterFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.*

open class BaseTest {

    @JvmField @Rule
    val mockBackend = MockWebServer()

    fun getSnpRepo(testClass: Class<*>, fixture: String): StudentAndParentRepository {
        mockBackend.enqueue(MockResponse().setBody(testClass.getResource(fixture).readText()))
        return StudentAndParentRepository(getService(StudentAndParentService::class.java))
    }

    fun <T> getService(service: Class<T>, url: String = mockBackend.url("/").toString(), html: Boolean = true, errorInterceptor: Boolean = true): T {
        return Retrofit.Builder()
                .client(OkHttpClient.Builder()
                        .apply {
                            if (errorInterceptor) addInterceptor(ErrorInterceptor())
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

    fun getLocalDate(year: Int, month: Int, day: Int): LocalDate {
        return LocalDate.of(year, month, day)
    }

    fun getLocalDateTime(year: Int, month: Int, day: Int, hour: Int = 0, minute: Int = 0, second: Int = 0): LocalDateTime {
        return LocalDateTime.of(year, month, day, hour, minute, second)
    }

    fun getDate(year: Int, month: Int, day: Int, hour: Int = 0, minute: Int = 0, second: Int = 0, mili: Int = 0): Date {
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, second)
            set(Calendar.MILLISECOND, mili)
        }.time
    }
}

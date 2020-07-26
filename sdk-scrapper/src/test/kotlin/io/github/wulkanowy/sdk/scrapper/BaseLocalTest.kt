package io.github.wulkanowy.sdk.scrapper

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import io.github.wulkanowy.sdk.scrapper.grades.DateDeserializer
import io.github.wulkanowy.sdk.scrapper.grades.GradeDate
import io.github.wulkanowy.sdk.scrapper.interceptor.AutoLoginInterceptor
import io.github.wulkanowy.sdk.scrapper.interceptor.ErrorInterceptor
import io.github.wulkanowy.sdk.scrapper.login.LoginHelper
import io.github.wulkanowy.sdk.scrapper.repository.StudentRepository
import io.github.wulkanowy.sdk.scrapper.service.LoginService
import io.github.wulkanowy.sdk.scrapper.service.StudentService
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import pl.droidsonroids.retrofit2.JspoonConverterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.CookieManager

abstract class BaseLocalTest : BaseTest() {

    val server = MockWebServer()
    val jsonParser = JsonParser()

    fun MockWebServer.enqueue(fileName: String, clazz: Class<*> = this@BaseLocalTest::class.java) {
        enqueue(MockResponse().setBody(clazz.getResource(fileName).readText()))
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    open fun getStudentRepo(testClass: Class<*>, fixture: String, loginType: Scrapper.LoginType = Scrapper.LoginType.STANDARD, autoLogin: Boolean = false): StudentRepository {
        server.enqueue(MockResponse().setBody(testClass.getResource(fixture).readText()))
        return StudentRepository(getService(StudentService::class.java, server.url("/").toString(), false, true, true, loginType, autoLogin))
    }

    fun <T> getService(
        service: Class<T>,
        url: String = this.server.url("/").toString(),
        html: Boolean = true,
        errorInterceptor: Boolean = true,
        noLoggedInInterceptor: Boolean = true,
        loginType: Scrapper.LoginType = Scrapper.LoginType.STANDARD,
        autoLogin: Boolean = false
    ): T {
        return Retrofit.Builder()
            .client(OkHttpClient.Builder()
                .apply {
                    if (errorInterceptor) addInterceptor(ErrorInterceptor())
                    if (noLoggedInInterceptor) addInterceptor(AutoLoginInterceptor(loginType, CookieManager(), false) {
                        if (autoLogin) runBlocking {
                            LoginHelper(loginType, "http", "localhost", "powiatwulkanowy", CookieManager(), getService(LoginService::class.java))
                                .login("jan", "kowalski")
                            true
                        } else false
                    })
                }
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build()
            )
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(if (!html) GsonConverterFactory.create(GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .serializeNulls()
                .registerTypeAdapter(GradeDate::class.java, DateDeserializer(GradeDate::class.java))
                .create()) else JspoonConverterFactory.create())
            .baseUrl(url)
            .build()
            .create(service)
    }
}

package io.github.wulkanowy.sdk.scrapper

import com.squareup.moshi.Moshi
import io.github.wulkanowy.sdk.scrapper.adapter.CustomDateAdapter
import io.github.wulkanowy.sdk.scrapper.adapter.GradeDateDeserializer
import io.github.wulkanowy.sdk.scrapper.interceptor.AutoLoginInterceptor
import io.github.wulkanowy.sdk.scrapper.interceptor.ErrorInterceptor
import io.github.wulkanowy.sdk.scrapper.interceptor.HttpErrorInterceptor
import io.github.wulkanowy.sdk.scrapper.login.LoginHelper
import io.github.wulkanowy.sdk.scrapper.repository.StudentRepository
import io.github.wulkanowy.sdk.scrapper.service.LoginService
import io.github.wulkanowy.sdk.scrapper.service.StudentService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import pl.droidsonroids.retrofit2.JspoonConverterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.CookieManager

abstract class BaseLocalTest : BaseTest() {

    val server = MockWebServer()

    fun MockWebServer.enqueue(fileName: String, clazz: Class<*> = this@BaseLocalTest::class.java) {
        enqueue(MockResponse().setBody(clazz.getResource(fileName).readText()))
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    fun getStudentRepo(testClass: Class<*>, fixture: String, loginType: Scrapper.LoginType = Scrapper.LoginType.STANDARD, autoLogin: Boolean = false): StudentRepository {
        return getStudentRepo(loginType, autoLogin) { it.enqueue(fixture, testClass) }
    }

    fun getStudentRepo(loginType: Scrapper.LoginType = Scrapper.LoginType.STANDARD, autoLogin: Boolean = false, responses: (MockWebServer) -> Unit): StudentRepository {
        responses(server)
        val okHttp = getOkHttp(errorInterceptor = true, autoLoginInterceptorOn = true, loginType = loginType, autoLogin = autoLogin)
        return StudentRepository(getService(StudentService::class.java, server.url("/").toString(), false, okHttp))
    }

    fun <T> getService(
        service: Class<T>,
        url: String = this.server.url("/").toString(),
        html: Boolean = true,
        okHttp: OkHttpClient = getOkHttp()
    ): T = Retrofit.Builder()
        .client(okHttp)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(
            if (!html) MoshiConverterFactory.create(
                Moshi.Builder()
                    .add(CustomDateAdapter())
                    .add(GradeDateDeserializer())
                    .build()
            ) else JspoonConverterFactory.create()
        )
        .baseUrl(url)
        .build()
        .create(service)

    fun getOkHttp(
        errorInterceptor: Boolean = true,
        autoLoginInterceptorOn: Boolean = true,
        loginType: Scrapper.LoginType = Scrapper.LoginType.STANDARD,
        autoLogin: Boolean = false,
        autoLoginInterceptor: AutoLoginInterceptor = getAutoLoginInterceptor(loginType, autoLogin)
    ): OkHttpClient = OkHttpClient.Builder()
        .apply {
            if (errorInterceptor) addInterceptor(ErrorInterceptor())
            if (autoLoginInterceptorOn) addInterceptor(autoLoginInterceptor)
        }
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
        .addInterceptor(HttpErrorInterceptor())
        .build()

    private fun getAutoLoginInterceptor(loginType: Scrapper.LoginType, autoLogin: Boolean): AutoLoginInterceptor {
        return AutoLoginInterceptor(loginType, CookieManager(), false) {
            if (autoLogin) LoginHelper(loginType, "http", "localhost", "powiatwulkanowy", CookieManager(), getService(LoginService::class.java))
                .login("jan", "kowalski")
        }
    }
}

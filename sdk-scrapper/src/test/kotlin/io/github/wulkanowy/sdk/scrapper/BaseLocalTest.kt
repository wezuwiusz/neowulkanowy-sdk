package io.github.wulkanowy.sdk.scrapper

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import io.github.wulkanowy.sdk.scrapper.adapter.ObjectSerializer
import io.github.wulkanowy.sdk.scrapper.interceptor.AutoLoginInterceptor
import io.github.wulkanowy.sdk.scrapper.interceptor.ErrorInterceptor
import io.github.wulkanowy.sdk.scrapper.interceptor.HttpErrorInterceptor
import io.github.wulkanowy.sdk.scrapper.login.LoginHelper
import io.github.wulkanowy.sdk.scrapper.repository.StudentRepository
import io.github.wulkanowy.sdk.scrapper.service.LoginService
import io.github.wulkanowy.sdk.scrapper.service.StudentService
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import pl.droidsonroids.retrofit2.JspoonConverterFactory
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.CookieManager

abstract class BaseLocalTest : BaseTest() {

    val server = MockWebServer()

    fun MockWebServer.enqueue(fileName: String, clazz: Class<*> = this@BaseLocalTest::class.java) {
        enqueue(MockResponse().setBody(clazz.getResource(fileName)!!.readText()))
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    internal fun getStudentRepo(
        testClass: Class<*>,
        fixture: String,
        loginType: Scrapper.LoginType = Scrapper.LoginType.STANDARD,
        autoLogin: Boolean = false,
    ): StudentRepository {
        return getStudentRepo(loginType, autoLogin) { it.enqueue(fixture, testClass) }
    }

    internal fun getStudentRepo(loginType: Scrapper.LoginType = Scrapper.LoginType.STANDARD, autoLogin: Boolean = false, responses: (MockWebServer) -> Unit): StudentRepository {
        responses(server)
        val okHttp = getOkHttp(errorInterceptor = true, autoLoginInterceptorOn = true, loginType = loginType, autoLogin = autoLogin)
        return StudentRepository(getService(StudentService::class.java, server.url("/").toString(), false, okHttp))
    }

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        explicitNulls = false
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
        serializersModule = SerializersModule {
            contextual(ObjectSerializer)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun <T> getService(
        service: Class<T>,
        url: String = this.server.url("/").toString(),
        html: Boolean = true,
        okHttp: OkHttpClient = getOkHttp(),
    ): T = Retrofit.Builder()
        .client(okHttp)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(
            if (!html) json.asConverterFactory("application/json".toMediaType())
            else JspoonConverterFactory.create(),
        )
        .baseUrl(url)
        .build()
        .create(service)

    internal fun getOkHttp(
        errorInterceptor: Boolean = true,
        autoLoginInterceptorOn: Boolean = true,
        loginType: Scrapper.LoginType = Scrapper.LoginType.STANDARD,
        autoLogin: Boolean = false,
        autoLoginInterceptor: AutoLoginInterceptor = getAutoLoginInterceptor(loginType, autoLogin),
    ): OkHttpClient = OkHttpClient.Builder()
        .apply {
            if (errorInterceptor) addInterceptor(ErrorInterceptor(CookieManager()))
            if (autoLoginInterceptorOn) addInterceptor(autoLoginInterceptor)
        }
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
        .addInterceptor(HttpErrorInterceptor())
        .build()

    private fun getAutoLoginInterceptor(loginType: Scrapper.LoginType, autoLogin: Boolean): AutoLoginInterceptor {
        return AutoLoginInterceptor(loginType, CookieManager()) {
            if (autoLogin) {
                LoginHelper(loginType, "http", "localhost", "powiatwulkanowy", CookieManager(), getService(LoginService::class.java))
                    .login("jan", "kowalski")
            }
        }
    }
}

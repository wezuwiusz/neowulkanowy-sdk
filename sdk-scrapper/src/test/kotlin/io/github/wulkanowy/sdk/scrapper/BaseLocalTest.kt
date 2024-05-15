package io.github.wulkanowy.sdk.scrapper

import io.github.wulkanowy.sdk.scrapper.adapter.ObjectSerializer
import io.github.wulkanowy.sdk.scrapper.interceptor.AutoLoginInterceptor
import io.github.wulkanowy.sdk.scrapper.interceptor.ErrorInterceptor
import io.github.wulkanowy.sdk.scrapper.interceptor.HttpErrorInterceptor
import io.github.wulkanowy.sdk.scrapper.login.LoginHelper
import io.github.wulkanowy.sdk.scrapper.login.LoginModuleResult
import io.github.wulkanowy.sdk.scrapper.login.LoginResult
import io.github.wulkanowy.sdk.scrapper.login.UrlGenerator
import io.github.wulkanowy.sdk.scrapper.repository.StudentPlusRepository
import io.github.wulkanowy.sdk.scrapper.repository.StudentRepository
import io.github.wulkanowy.sdk.scrapper.service.LoginService
import io.github.wulkanowy.sdk.scrapper.service.StudentPlusService
import io.github.wulkanowy.sdk.scrapper.service.StudentService
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.jsoup.nodes.Document
import org.junit.After
import pl.droidsonroids.retrofit2.JspoonConverterFactory
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.URL

abstract class BaseLocalTest : BaseTest() {

    val server = MockWebServer()

    fun MockWebServer.enqueue(fileName: String, clazz: Class<*> = this@BaseLocalTest::class.java, responseCode: Int = 200) {
        enqueue(
            MockResponse()
                .setBody(clazz.getResource(fileName)!!.readText())
                .setResponseCode(responseCode),
        )
    }

    fun MockWebServer.enqueueContent(content: String, responseCode: Int = 200) {
        enqueue(MockResponse().setBody(content).setResponseCode(responseCode))
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
        return StudentRepository(
            api = getService(StudentService::class.java, server.url("/").toString(), false, okHttp),
            urlGenerator = UrlGenerator.EMPTY,
        )
    }

    internal fun getStudentPlusRepo(
        testClass: Class<*>,
        fixture: String,
        loginType: Scrapper.LoginType = Scrapper.LoginType.STANDARD,
        autoLogin: Boolean = false,
    ): StudentPlusRepository {
        return getStudentPlusRepo(loginType, autoLogin) { it.enqueue(fixture, testClass) }
    }

    internal fun getStudentPlusRepo(
        loginType: Scrapper.LoginType = Scrapper.LoginType.STANDARD,
        autoLogin: Boolean = false,
        responses: (MockWebServer) -> Unit,
    ): StudentPlusRepository {
        responses(server)
        val okHttp = getOkHttp(errorInterceptor = true, autoLoginInterceptorOn = true, loginType = loginType, autoLogin = autoLogin)
        return StudentPlusRepository(
            api = getService(StudentPlusService::class.java, server.url("/").toString(), false, okHttp),
        )
    }

    @OptIn(ExperimentalSerializationApi::class)
    val json = Json {
        explicitNulls = false
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
        serializersModule = SerializersModule {
            contextual(ObjectSerializer)
        }
    }

    fun <T> getService(
        service: Class<T>,
        url: String = this.server.url("/").toString(),
        html: Boolean = true,
        okHttp: OkHttpClient = getOkHttp(),
    ): T = Retrofit.Builder()
        .client(okHttp)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(
            when {
                !html -> json.asConverterFactory("application/json".toMediaType())
                else -> JspoonConverterFactory.create()
            },
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
            if (errorInterceptor) addInterceptor(ErrorInterceptor(CookieJarCabinet()))
            if (autoLoginInterceptorOn) addInterceptor(autoLoginInterceptor)
        }
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
        .addInterceptor(HttpErrorInterceptor())
        .build()

    private fun getAutoLoginInterceptor(loginType: Scrapper.LoginType, autoLogin: Boolean): AutoLoginInterceptor {
        val urlGenerator = UrlGenerator(URL("http://localhost/"), "", "lodz", "")
        return AutoLoginInterceptor(
            loginType = loginType,
            cookieJarCabinet = CookieJarCabinet(),
            notLoggedInCallback = {
                when {
                    !autoLogin -> LoginResult(
                        isStudentSchoolUseEduOne = false,
                        studentSchools = emptyList(),
                    )

                    else -> LoginHelper(
                        loginType = loginType,
                        schema = "http",
                        host = "localhost",
                        domainSuffix = "",
                        symbol = "powiatwulkanowy",
                        cookieJarCabinet = CookieJarCabinet(),
                        api = getService(LoginService::class.java),
                        urlGenerator = urlGenerator,
                    ).login("jan@fakelog.cf", "jan123")
                }
            },
            fetchModuleCookies = { _ -> LoginModuleResult("http://localhost".toHttpUrl(), Document("")) },
            json = json,
        )
    }
}

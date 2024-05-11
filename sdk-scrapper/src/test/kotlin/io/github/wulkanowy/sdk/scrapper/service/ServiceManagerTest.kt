package io.github.wulkanowy.sdk.scrapper.service

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import io.github.wulkanowy.sdk.scrapper.CookieJarCabinet
import io.github.wulkanowy.sdk.scrapper.OkHttpClientBuilderFactory
import io.github.wulkanowy.sdk.scrapper.Scrapper
import io.github.wulkanowy.sdk.scrapper.exception.ScrapperException
import io.github.wulkanowy.sdk.scrapper.exception.ServiceUnavailableException
import io.github.wulkanowy.sdk.scrapper.interceptor.ErrorInterceptorTest
import io.github.wulkanowy.sdk.scrapper.login.LoginTest
import io.github.wulkanowy.sdk.scrapper.notes.NotesTest
import kotlinx.coroutines.runBlocking
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.URL
import java.util.concurrent.locks.ReentrantLock

class ServiceManagerTest : BaseLocalTest() {

    @Test
    fun interceptorTest() {
        val manager = ServiceManager(
            okHttpClientBuilderFactory = OkHttpClientBuilderFactory(),
            cookieJarCabinet = CookieJarCabinet(),
            logLevel = HttpLoggingInterceptor.Level.NONE,
            loginType = Scrapper.LoginType.STANDARD,
            schema = "http",
            host = "fakelog.localhost:3000",
            domainSuffix = "",
            symbol = "default",
            email = "email",
            password = "password",
            schoolId = "schoolSymbol",
            studentId = 123,
            diaryId = 101,
            kindergartenDiaryId = 0,
            schoolYear = 2019,
            emptyCookieJarIntercept = false,
            androidVersion = "",
            buildTag = "",
            userAgentTemplate = "",
            loginLock = ReentrantLock(true),
            headersByHost = mutableMapOf(),
        )
        manager.setInterceptor({ throw ScrapperException("Test") })

        try {
            runBlocking { manager.getStudentService().getNotes() }
        } catch (e: Throwable) {
            assertTrue(e is ScrapperException)
        }
    }

    @Test
    fun interceptorTest_prepend() {
        server.enqueue(MockResponse().setBody(NotesTest::class.java.getResource("UwagiIOsiagniecia.json").readText()))
        server.start(3000)
        val manager = ServiceManager(
            okHttpClientBuilderFactory = OkHttpClientBuilderFactory(),
            cookieJarCabinet = CookieJarCabinet(),
            logLevel = HttpLoggingInterceptor.Level.NONE,
            loginType = Scrapper.LoginType.STANDARD,
            schema = "http",
            host = "fakelog.localhost:3000",
            domainSuffix = "",
            symbol = "default",
            email = "email",
            password = "password",
            schoolId = "schoolSymbol",
            studentId = 123,
            diaryId = 101,
            kindergartenDiaryId = 0,
            schoolYear = 2019,
            emptyCookieJarIntercept = false,
            androidVersion = "",
            buildTag = "",
            userAgentTemplate = "",
            loginLock = ReentrantLock(true),
            headersByHost = mutableMapOf(),
        )
        manager.setInterceptor(
            {
                // throw IOException("Test")
                it.proceed(it.request())
            },
        )
        manager.setInterceptor({ throw ScrapperException("Test") }, false)

        try {
            runBlocking { manager.getStudentService().getNotes() }
        } catch (e: Throwable) {
            assertTrue(e is ScrapperException)
        }
    }

    @Test
    fun apiNormalizedSymbol_blank() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("LoginPage-standard.html").readText().replace("fakelog.cf", "fakelog.localhost:3000")))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(ErrorInterceptorTest::class.java.getResource("Offline.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.start(3000)

        val api = Scrapper().apply {
            logLevel = HttpLoggingInterceptor.Level.BASIC
            ssl = false
            host = "fakelog.localhost:3000"
            email = "jan@fakelog.cf"
            password = "jan123"
            symbol = ""
        }

        try {
            runBlocking { api.getUserSubjects() }
        } catch (e: Throwable) {
            assertTrue(e is ScrapperException)
        }

        server.takeRequest()
        // /Default/Account/LogOn <– default symbol set
        assertEquals("/Default/Account/LogOn", URL(server.takeRequest().requestUrl.toString()).path)
    }

    @Test
    fun autoLoginInterceptor() {
        server.enqueue(MockResponse().setResponseCode(503))
        server.start(3000)
        val manager = ServiceManager(
            okHttpClientBuilderFactory = OkHttpClientBuilderFactory(),
            cookieJarCabinet = CookieJarCabinet(),
            logLevel = HttpLoggingInterceptor.Level.NONE,
            loginType = Scrapper.LoginType.STANDARD,
            schema = "http",
            host = "fakelog.localhost:3000",
            domainSuffix = "",
            symbol = "default",
            email = "email",
            password = "password",
            schoolId = "schoolSymbol",
            studentId = 123,
            diaryId = 101,
            kindergartenDiaryId = 0,
            schoolYear = 2019,
            emptyCookieJarIntercept = false,
            androidVersion = "",
            buildTag = "",
            userAgentTemplate = "",
            loginLock = ReentrantLock(true),
            headersByHost = mutableMapOf(),
        )

        val res = runCatching {
            runBlocking { manager.getStudentService().getNotes() }
        }

        val exception = res.exceptionOrNull()!!

        assertEquals("503: Server Error", exception.message)
        assertEquals(ServiceUnavailableException::class.java, exception::class.java)
    }
}

package io.github.wulkanowy.sdk.scrapper.service

import io.github.wulkanowy.sdk.scrapper.Scrapper
import io.github.wulkanowy.sdk.scrapper.ScrapperException
import io.github.wulkanowy.sdk.scrapper.BaseTest
import io.github.wulkanowy.sdk.scrapper.OkHttpClientBuilderFactory
import io.github.wulkanowy.sdk.scrapper.interceptor.ErrorInterceptorTest
import io.github.wulkanowy.sdk.scrapper.login.LoginTest
import io.github.wulkanowy.sdk.scrapper.notes.NotesResponse
import io.github.wulkanowy.sdk.scrapper.notes.NotesTest
import io.github.wulkanowy.sdk.scrapper.register.Student
import io.reactivex.observers.TestObserver
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.net.URL

class ServiceManagerTest : BaseTest() {

    lateinit var server: MockWebServer

    @Before
    fun setUp() {
        server = MockWebServer()
    }

    @After
    fun shutDown() {
        server.shutdown()
    }

    @Test
    fun interceptorTest() {
        val manager = ServiceManager(OkHttpClientBuilderFactory(), HttpLoggingInterceptor.Level.NONE,
                Scrapper.LoginType.STANDARD, "http", "fakelog.localhost:3000", "default", "email", "password",
                "schoolSymbol", 123, 101, 2019, "", ""
        )
        manager.setInterceptor(Interceptor {
            throw ScrapperException("Test")
        })

        val notes = manager.getSnpService().getNotes()
        val observer = TestObserver<NotesResponse>()
        notes.subscribe(observer)
        observer.assertTerminated()
        observer.assertNotComplete()
        observer.assertError(ScrapperException::class.java)
    }

    @Test
    fun interceptorTest_prepend() {
        server.enqueue(MockResponse().setBody(NotesTest::class.java.getResource("UwagiOsiagniecia-filled.html").readText()))
        server.start(3000)
        val manager = ServiceManager(OkHttpClientBuilderFactory(), HttpLoggingInterceptor.Level.NONE,
                Scrapper.LoginType.STANDARD, "http", "fakelog.localhost:3000", "default", "email", "password",
                "schoolSymbol", 123, 101, 2019, "", ""
        )
        manager.setInterceptor(Interceptor {
            // throw IOException("Test")
            it.proceed(it.request())
        })
        manager.setInterceptor(Interceptor {
            throw ScrapperException("Test")
        }, false)

        val notes = manager.getSnpService().getNotes()
        val observer = TestObserver<NotesResponse>()
        notes.subscribe(observer)
        observer.assertTerminated()
        observer.assertNotComplete()
        observer.assertError(ScrapperException::class.java)
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

        val pupils = api.getStudents()
        val observer = TestObserver<List<Student>>()
        pupils.subscribe(observer)
        observer.assertTerminated()
        observer.assertError(ScrapperException::class.java)

        server.takeRequest()
        // /Default/Account/LogOn <– default symbol set
        assertEquals("/Default/Account/LogOn", URL(server.takeRequest().requestUrl.toString()).path)
    }
}

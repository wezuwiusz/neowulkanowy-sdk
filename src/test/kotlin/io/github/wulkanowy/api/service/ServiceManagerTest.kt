package io.github.wulkanowy.api.service

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.api.ApiException
import io.github.wulkanowy.api.BaseTest
import io.github.wulkanowy.api.login.LoginTest
import io.github.wulkanowy.api.notes.NotesResponse
import io.github.wulkanowy.api.register.Pupil
import io.reactivex.observers.TestObserver
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.IOException
import java.net.URL

class ServiceManagerTest : BaseTest() {

    @Test
    fun interceptorTest() {
        val manager = ServiceManager(HttpLoggingInterceptor.Level.NONE,
                "http", "fakelog.localhost:3000", "default", "email", "password",
                "schoolId", "studentId", "diaryId"
        )
        manager.setInterceptor(Interceptor {
            throw ApiException("Test")
        })

        val notes = manager.getSnpService().getNotes()
        val observer = TestObserver<NotesResponse>()
        notes.subscribe(observer)
        observer.assertTerminated()
        observer.assertNotComplete()
        observer.assertError(ApiException::class.java)
    }

    @Test
    fun interceptorTest_prepend() {
        val manager = ServiceManager(HttpLoggingInterceptor.Level.NONE,
                "http", "fakelog.localhost:3000", "default", "email", "password",
                "schoolId", "studentId", "diaryId"
        )
        manager.setInterceptor(Interceptor {
            throw IOException("Test")
        })
        manager.setInterceptor(Interceptor {
            throw ApiException("Test")
        }, 0)

        val notes = manager.getSnpService().getNotes()
        val observer = TestObserver<NotesResponse>()
        notes.subscribe(observer)
        observer.assertTerminated()
        observer.assertNotComplete()
        observer.assertError(ApiException::class.java)
    }

    @Test
    fun blankSymbol() {
        val server = MockWebServer()
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Offline.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.start(3000)

        val api = Api().apply {
            ssl = false
            host = "fakelog.localhost:3000"
            email = "jan@fakelog.cf"
            password = "jan123"
            symbol = ""
        }

        val pupils = api.getPupils()
        val observer = TestObserver<List<Pupil>>()
        pupils.subscribe(observer)
        observer.assertTerminated()
        observer.assertError(ApiException::class.java)

        // /Default/Account/LogOn <– default symbol set
        assertEquals("/Default/Account/LogOn", URL(server.takeRequest().requestUrl.toString()).path)

        server.shutdown()
    }
}

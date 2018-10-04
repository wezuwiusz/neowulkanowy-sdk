package io.github.wulkanowy.api.repository

import io.github.wulkanowy.api.BaseTest
import io.github.wulkanowy.api.ApiException
import io.github.wulkanowy.api.login.LoginTest
import io.github.wulkanowy.api.register.Pupil
import io.github.wulkanowy.api.service.LoginService
import io.github.wulkanowy.api.service.StudentAndParentService
import io.reactivex.observers.TestObserver
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test

class RegisterRepositoryTest : BaseTest() {

    private val normal by lazy {
        RegisterRepository("Default", "jan@fakelog.cf", "jan123",
                LoginRepository("http", "fakelog.localhost:3000", "default",
                        getService(LoginService::class.java, "http://fakelog.localhost:3000/")),
                getService(StudentAndParentService::class.java)
        )
    }

    private lateinit var server: MockWebServer

    @Before
    fun setUp() {
        server = MockWebServer()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun normalVulcanException() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Offline.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.start(3000)

        val res = normal.getPupils()
        val observer = TestObserver<List<Pupil>>()
        res.subscribe(observer)
        observer.assertTerminated()
        observer.assertError(ApiException::class.java)
    }
}

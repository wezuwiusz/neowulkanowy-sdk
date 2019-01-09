package io.github.wulkanowy.api.repository

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.api.ApiException
import io.github.wulkanowy.api.BaseLocalTest
import io.github.wulkanowy.api.interceptor.ErrorInterceptorTest
import io.github.wulkanowy.api.login.LoginTest
import io.github.wulkanowy.api.register.Pupil
import io.github.wulkanowy.api.service.*
import io.reactivex.observers.TestObserver
import okhttp3.mockwebserver.MockResponse
import org.junit.Test
import java.net.CookieManager

class RegisterRepositoryTest : BaseLocalTest() {

    private val normal by lazy {
        RegisterRepository("Default", "jan@fakelog.cf", "jan123", false,
                LoginRepository(Api.LoginType.STANDARD, "http", "fakelog.localhost:3000", "Default",  CookieManager(),
                        getService(LoginService::class.java, "http://fakelog.localhost:3000/")),
                getService(service = RegisterService::class.java, url = "http://fakelog.localhost:3000/", errorInterceptor = false, noLoggedInInterceptor = false),
                getService(StudentAndParentService::class.java),
                getService(StudentService::class.java),
                ServiceManager.UrlGenerator("http", "fakelog.localhost:3000", "Default", "")
        )
    }

    @Test
    fun normalVulcanException() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("LoginPage-standard.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(ErrorInterceptorTest::class.java.getResource("Offline.html").readText()))
        server.start(3000)

        val res = normal.getPupils()
        val observer = TestObserver<List<Pupil>>()
        res.subscribe(observer)
        observer.assertTerminated()
        observer.assertError(ApiException::class.java)
    }

    @Test
    fun filterSymbolsWithSpaces() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("LoginPage-standard.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))

        (0..4).onEach {
            server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Login-success-old.html").readText()))
            server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("LoginPage-standard.html").readText()))
            server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("SnP-start.html").readText()))
        }

        server.start(3000)

        val res = normal.getPupils()
        val observer = TestObserver<List<Pupil>>()
        res.subscribe(observer)
        observer.assertComplete()
    }
}

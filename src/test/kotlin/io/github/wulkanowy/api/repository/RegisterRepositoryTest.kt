package io.github.wulkanowy.api.repository

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.api.ApiException
import io.github.wulkanowy.api.BaseLocalTest
import io.github.wulkanowy.api.interceptor.ErrorInterceptorTest
import io.github.wulkanowy.api.login.LoginHelper
import io.github.wulkanowy.api.login.LoginTest
import io.github.wulkanowy.api.register.Student
import io.github.wulkanowy.api.service.LoginService
import io.github.wulkanowy.api.service.RegisterService
import io.github.wulkanowy.api.service.ServiceManager
import io.github.wulkanowy.api.service.StudentAndParentService
import io.github.wulkanowy.api.service.StudentService
import io.reactivex.observers.TestObserver
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Test
import java.net.CookieManager

class RegisterRepositoryTest : BaseLocalTest() {

    private val normal by lazy { getRegisterRepository("Default") }

    private fun getRegisterRepository(symbol: String): RegisterRepository {
        return RegisterRepository(symbol, "jan@fakelog.cf", "jan123", false,
            LoginHelper(Api.LoginType.STANDARD, "http", "fakelog.localhost:3000", symbol, CookieManager(),
                getService(LoginService::class.java, "http://fakelog.localhost:3000/")),
            getService(service = RegisterService::class.java, url = "http://fakelog.localhost:3000/", errorInterceptor = false, noLoggedInInterceptor = false),
            getService(StudentAndParentService::class.java),
            getService(StudentService::class.java),
            ServiceManager.UrlGenerator("http", "fakelog.localhost:3000", symbol, "")
        )
    }

    @Test
    fun normalVulcanException() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("LoginPage-standard.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(ErrorInterceptorTest::class.java.getResource("Offline.html").readText()))
        server.start(3000)

        val res = normal.getStudents()
        val observer = TestObserver<List<Student>>()
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

        val res = normal.getStudents()
        val observer = TestObserver<List<Student>>()
        res.subscribe(observer)
        observer.assertComplete()
    }

    @Test
    fun normalizeInvalidSymbol_default() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("LoginPage-standard.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(ErrorInterceptorTest::class.java.getResource("Offline.html").readText()))

        server.start(3000)

        val res = getRegisterRepository("Default").getStudents()
        val observer = TestObserver<List<Student>>()
        res.subscribe(observer)
        observer.assertTerminated()

        assertEquals("/Default/Account/LogOn", server.takeRequest().path)
    }

    @Test
    fun normalizeInvalidSymbol_custom() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("LoginPage-standard.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(ErrorInterceptorTest::class.java.getResource("Offline.html").readText()))

        server.start(3000)

        val res = getRegisterRepository(" Rzeszów + ").getStudents()
        val observer = TestObserver<List<Student>>()
        res.subscribe(observer)
        observer.assertTerminated()

        assertEquals("/rzeszow/Account/LogOn", server.takeRequest().path)
    }

    @Test
    fun normalizeInvalidSymbol_trimMultipleSpaces() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("LoginPage-standard.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(ErrorInterceptorTest::class.java.getResource("Offline.html").readText()))

        server.start(3000)

        val res = getRegisterRepository(" Niepoprawny    symbol no ale + ").getStudents()
        val observer = TestObserver<List<Student>>()
        res.subscribe(observer)
        observer.assertTerminated()

        assertEquals("/niepoprawnysymbolnoale/Account/LogOn", server.takeRequest().path)
    }

    @Test
    fun normalizeInvalidSymbol_emptyFallback() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("LoginPage-standard.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(ErrorInterceptorTest::class.java.getResource("Offline.html").readText()))

        server.start(3000)

        val res = getRegisterRepository(" + ").getStudents()
        val observer = TestObserver<List<Student>>()
        res.subscribe(observer)
        observer.assertTerminated()

        assertEquals("/Default/Account/LogOn", server.takeRequest().path)
    }
}

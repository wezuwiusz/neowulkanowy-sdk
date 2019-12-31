package io.github.wulkanowy.sdk.scrapper.repository

import io.github.wulkanowy.sdk.scrapper.Scrapper
import io.github.wulkanowy.sdk.scrapper.ScrapperException
import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import io.github.wulkanowy.sdk.scrapper.interceptor.ErrorInterceptorTest
import io.github.wulkanowy.sdk.scrapper.login.LoginHelper
import io.github.wulkanowy.sdk.scrapper.login.LoginTest
import io.github.wulkanowy.sdk.scrapper.register.RegisterTest
import io.github.wulkanowy.sdk.scrapper.register.Student
import io.github.wulkanowy.sdk.scrapper.service.LoginService
import io.github.wulkanowy.sdk.scrapper.service.RegisterService
import io.github.wulkanowy.sdk.scrapper.service.ServiceManager
import io.github.wulkanowy.sdk.scrapper.service.StudentAndParentService
import io.github.wulkanowy.sdk.scrapper.service.StudentService
import io.reactivex.observers.TestObserver
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.CookieManager

class RegisterRepositoryTest : BaseLocalTest() {

    private val normal by lazy { getRegisterRepository("Default") }

    private fun getRegisterRepository(symbol: String, useNewStudent: Boolean = false): RegisterRepository {
        return RegisterRepository(symbol, "jan@fakelog.cf", "jan123", useNewStudent,
            LoginHelper(Scrapper.LoginType.STANDARD, "http", "fakelog.localhost:3000", symbol, CookieManager(),
                getService(LoginService::class.java, "http://fakelog.localhost:3000/")),
            getService(service = RegisterService::class.java, url = "http://fakelog.localhost:3000/", errorInterceptor = false, noLoggedInInterceptor = false),
            getService(StudentAndParentService::class.java),
            getService(service = StudentService::class.java, html = !useNewStudent),
            ServiceManager.UrlGenerator("http", "fakelog.localhost:3000", symbol, "")
        )
    }

    @Test
    fun normalLogin() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("LoginPage-standard.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Login-success-triple.html").readText()))

        (0..2).onEach {
            server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("LoginPage-standard.html").readText()))
            server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("WitrynaUcznia.html").readText()))
            server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("UczenCache.json").readText()))
            server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("UczenDziennik-no-semester.json").readText()))
        }

        (0..5).onEach { // 5x symbol
            server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        }
        server.start(3000)

        val res = getRegisterRepository("Default", true).getStudents()
        val observer = TestObserver<List<Student>>()
        res.subscribe(observer)
        observer.assertComplete()
        val students = observer.values()[0]
        assertEquals(3, students.size)
    }

    @Test
    fun normalLogin_temporarilyOff() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("LoginPage-standard.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Login-success-triple.html").readText()))

        (0..1).onEach {
            server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("LoginPage-standard.html").readText()))
            server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("WitrynaUcznia.html").readText()))
            server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("UczenCache.json").readText()))
            server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("UczenDziennik-no-semester.json").readText()))
        }

        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("LoginPage-standard.html").readText()))
        server.enqueue(MockResponse().setBody(ErrorInterceptorTest::class.java.getResource("AplikacjaCzasowoWyłączona.html").readText()))

        (0..5).onEach { // 5x symbol
            server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        }
        server.start(3000)

        val res = getRegisterRepository("Default", true).getStudents()
        val observer = TestObserver<List<Student>>()
        res.subscribe(observer)
        observer.assertComplete()
        val students = observer.values()[0]
        assertEquals(2, students.size)
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
        observer.assertError(ScrapperException::class.java)
    }

    @Test
    fun filterSymbolsWithSpaces() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("LoginPage-standard.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))

        (0..5).onEach {
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

    @Test
    fun normalizeInvalidSymbol_digits() {
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
        assertTrue(server.takeRequest().path.startsWith("/Account/LogOn?ReturnUrl=%2FDefault"))
        assertEquals("/default/LoginEndpoint.aspx", server.takeRequest().path)
        assertEquals("/glubczyce2/LoginEndpoint.aspx", server.takeRequest().path)
    }
}

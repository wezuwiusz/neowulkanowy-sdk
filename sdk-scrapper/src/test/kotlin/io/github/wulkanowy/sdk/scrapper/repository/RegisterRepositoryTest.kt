package io.github.wulkanowy.sdk.scrapper.repository

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import io.github.wulkanowy.sdk.scrapper.Scrapper
import io.github.wulkanowy.sdk.scrapper.exception.ScrapperException
import io.github.wulkanowy.sdk.scrapper.exception.VulcanException
import io.github.wulkanowy.sdk.scrapper.interceptor.ErrorInterceptorTest
import io.github.wulkanowy.sdk.scrapper.login.LoginHelper
import io.github.wulkanowy.sdk.scrapper.login.LoginTest
import io.github.wulkanowy.sdk.scrapper.messages.MessagesTest
import io.github.wulkanowy.sdk.scrapper.register.RegisterTest
import io.github.wulkanowy.sdk.scrapper.service.LoginService
import io.github.wulkanowy.sdk.scrapper.service.MessagesService
import io.github.wulkanowy.sdk.scrapper.service.RegisterService
import io.github.wulkanowy.sdk.scrapper.service.ServiceManager
import io.github.wulkanowy.sdk.scrapper.service.StudentService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.CookieManager

class RegisterRepositoryTest : BaseLocalTest() {

    private val normal by lazy { getRegisterRepository("Default") }

    private fun getRegisterRepository(symbol: String): RegisterRepository {
        return RegisterRepository(
            startSymbol = symbol,
            email = "jan@fakelog.cf",
            password = "jan123",
            loginHelper = LoginHelper(
                Scrapper.LoginType.STANDARD, "http", "fakelog.localhost:3000", symbol, CookieManager(),
                getService(LoginService::class.java, "http://fakelog.localhost:3000/")
            ),
            register = getService(service = RegisterService::class.java, url = "http://fakelog.localhost:3000/", okHttp = getOkHttp(errorInterceptor = false, autoLoginInterceptorOn = false)),
            messages = getService(service = MessagesService::class.java, html = false),
            student = getService(service = StudentService::class.java, html = false),
            url = ServiceManager.UrlGenerator("http", "fakelog.localhost:3000", symbol, "")
        )
    }

    @Test
    fun normalLogin_one() {
        server.enqueue("LoginPage-standard.html", LoginTest::class.java)
        server.enqueue("Logowanie-uonet.html", LoginTest::class.java)
        server.enqueue("Login-success.html", LoginTest::class.java)

        server.enqueue("JednostkiUzytkownika.json", MessagesTest::class.java)
        server.enqueue("LoginPage-standard.html", LoginTest::class.java)
        server.enqueue("WitrynaUcznia.html", RegisterTest::class.java)
        server.enqueue("UczenCache.json", RegisterTest::class.java)
        server.enqueue("UczenDziennik-no-semester.json", RegisterTest::class.java)

        (1..4).onEach { // 4x symbol
            server.enqueue("Logowanie-brak-dostepu.html", LoginTest::class.java)
        }
        server.start(3000)

        val students = runBlocking { getRegisterRepository("Default").getStudents() }

        assertEquals(1, students.size)
        with(students[0]) {
            assertEquals("012345", schoolSymbol)
            assertEquals("", schoolShortName)
            assertEquals(2, semesters.size)
        }
    }

    @Test
    fun normalLogin_semesters() {
        server.enqueue("LoginPage-standard.html", LoginTest::class.java)
        server.enqueue("Logowanie-uonet.html", LoginTest::class.java)
        server.enqueue("Login-success.html", LoginTest::class.java)

        server.enqueue("JednostkiUzytkownika.json", MessagesTest::class.java)
        server.enqueue("LoginPage-standard.html", LoginTest::class.java)
        server.enqueue("WitrynaUcznia.html", RegisterTest::class.java)
        server.enqueue("UczenCache.json", RegisterTest::class.java)
        server.enqueue("UczenDziennik.json", RegisterTest::class.java)

        (1..4).onEach { // 4x symbol
            server.enqueue("Logowanie-brak-dostepu.html", LoginTest::class.java)
        }
        server.start(3000)

        val students = runBlocking { getRegisterRepository("Default").getStudents() }

        assertEquals(2, students.size)
        with(students[0]) {
            assertEquals("012345", schoolSymbol)
            assertEquals("", schoolShortName)
            assertEquals(6, semesters.size)
        }
        assertEquals(6, students[1].semesters.size)
    }

    @Test
    fun normalLogin_triple() {
        server.enqueue("LoginPage-standard.html", LoginTest::class.java)
        server.enqueue("Logowanie-uonet.html", LoginTest::class.java)
        server.enqueue("Login-success-triple.html", LoginTest::class.java)

        server.enqueue("JednostkiUzytkownika.json", MessagesTest::class.java)
        (0..2).onEach {
            server.enqueue("LoginPage-standard.html", LoginTest::class.java)
            server.enqueue("WitrynaUcznia.html", RegisterTest::class.java)
            server.enqueue("UczenCache.json", RegisterTest::class.java)
            server.enqueue("UczenDziennik-no-semester.json", RegisterTest::class.java)
        }

        (1..4).onEach { // 4x symbol
            server.enqueue("Logowanie-brak-dostepu.html", LoginTest::class.java)
        }
        server.start(3000)

        val students = runBlocking { getRegisterRepository("Default").getStudents() }
        assertEquals(3, students.size)

        with(students[0]) {
            assertEquals("000788", schoolSymbol)
            assertEquals("ZST-CKZiU", schoolShortName)
        }

        with(students[1]) {
            assertEquals("004355", schoolSymbol)
            assertEquals("ZSET", schoolShortName)
        }

        with(students[2]) {
            assertEquals("016636", schoolSymbol)
            assertEquals("G7 Wulkanowo", schoolShortName)
        }
    }

    @Test
    fun normalLogin_temporarilyOff() {
        server.enqueue("LoginPage-standard.html", LoginTest::class.java)
        server.enqueue("Logowanie-uonet.html", LoginTest::class.java)
        server.enqueue("Login-success-triple.html", LoginTest::class.java)

        server.enqueue("JednostkiUzytkownika.json", MessagesTest::class.java)
        (0..1).onEach {
            server.enqueue("LoginPage-standard.html", LoginTest::class.java)
            server.enqueue("WitrynaUcznia.html", RegisterTest::class.java)
            server.enqueue("UczenCache.json", RegisterTest::class.java)
            server.enqueue("UczenDziennik-no-semester.json", RegisterTest::class.java)
        }

        server.enqueue("LoginPage-standard.html", LoginTest::class.java)
        server.enqueue("AplikacjaCzasowoWyłączona.html", ErrorInterceptorTest::class.java)

        (1..4).onEach { // 4x symbol
            server.enqueue("Logowanie-brak-dostepu.html", LoginTest::class.java)
        }
        server.start(3000)

        val students = runBlocking { getRegisterRepository("Default").getStudents() }
        assertEquals(2, students.size)
    }

    @Test
    fun normalVulcanException() {
        server.enqueue("LoginPage-standard.html", LoginTest::class.java)
        server.enqueue("Logowanie-uonet.html", LoginTest::class.java)
        server.enqueue("Logowanie-brak-dostepu.html", LoginTest::class.java)
        server.enqueue("Offline.html", ErrorInterceptorTest::class.java)
        server.start(3000)

        try {
            runBlocking { normal.getStudents() }
        } catch (e: Throwable) {
            assertTrue(e is ScrapperException)
        }
    }

    @Test
    fun filterSymbolsWithSpaces() {
        server.enqueue("LoginPage-standard.html", LoginTest::class.java)
        server.enqueue("Logowanie-uonet.html", LoginTest::class.java)

        (1..5).onEach {
            server.enqueue("Login-success-old.html", LoginTest::class.java)
            server.enqueue("JednostkiUzytkownika.json", MessagesTest::class.java)
        }

        server.start(3000)

        runBlocking { normal.getStudents() }
    }

    @Test
    fun normalizeInvalidSymbol_default() {
        server.enqueue("LoginPage-standard.html", LoginTest::class.java)
        server.enqueue("Logowanie-uonet.html", LoginTest::class.java)
        server.enqueue("Logowanie-brak-dostepu.html", LoginTest::class.java)
        server.enqueue("Offline.html", ErrorInterceptorTest::class.java)

        server.start(3000)

        try {
            runBlocking { getRegisterRepository("Default").getStudents() }
        } catch (e: Throwable) {
            assertTrue(e is VulcanException)
            val expected = "Wystąpił nieoczekiwany błąd. Wystąpił błąd aplikacji. Prosimy zalogować się ponownie. Jeśli problem będzie się powtarzał, prosimy o kontakt z serwisem."
            assertEquals(expected, e.message)
        }

        assertEquals("/Default/Account/LogOn", server.takeRequest().path)
    }

    @Test
    fun normalizeInvalidSymbol_custom() {
        server.enqueue("LoginPage-standard.html", LoginTest::class.java)
        server.enqueue("Logowanie-uonet.html", LoginTest::class.java)
        server.enqueue("Logowanie-brak-dostepu.html", LoginTest::class.java)
        server.enqueue("Offline.html", ErrorInterceptorTest::class.java)

        server.start(3000)

        try {
            runBlocking { getRegisterRepository(" Rzeszów + ").getStudents() }
        } catch (e: Throwable) {
            assertTrue(e is VulcanException)
            val expected = "Wystąpił nieoczekiwany błąd. Wystąpił błąd aplikacji. Prosimy zalogować się ponownie. Jeśli problem będzie się powtarzał, prosimy o kontakt z serwisem."
            assertEquals(expected, e.message)
        }

        assertEquals("/rzeszow/Account/LogOn", server.takeRequest().path)
    }

    @Test
    fun normalizeInvalidSymbol_trimMultipleSpaces() {
        server.enqueue("LoginPage-standard.html", LoginTest::class.java)
        server.enqueue("Logowanie-uonet.html", LoginTest::class.java)
        server.enqueue("Logowanie-brak-dostepu.html", LoginTest::class.java)
        server.enqueue("Offline.html", ErrorInterceptorTest::class.java)

        server.start(3000)

        try {
            runBlocking { getRegisterRepository(" Niepoprawny    symbol no ale + ").getStudents() }
        } catch (e: Throwable) {
            assertTrue(e is VulcanException)
            val expected = "Wystąpił nieoczekiwany błąd. Wystąpił błąd aplikacji. Prosimy zalogować się ponownie. Jeśli problem będzie się powtarzał, prosimy o kontakt z serwisem."
            assertEquals(expected, e.message)
        }

        assertEquals("/niepoprawnysymbolnoale/Account/LogOn", server.takeRequest().path)
    }

    @Test
    fun normalizeInvalidSymbol_emptyFallback() {
        server.enqueue("LoginPage-standard.html", LoginTest::class.java)
        server.enqueue("Logowanie-uonet.html", LoginTest::class.java)
        server.enqueue("Logowanie-brak-dostepu.html", LoginTest::class.java)
        server.enqueue("Offline.html", ErrorInterceptorTest::class.java)

        server.start(3000)

        try {
            runBlocking { getRegisterRepository(" + ").getStudents() }
        } catch (e: Throwable) {
            assertTrue(e is VulcanException)
            val expected = "Wystąpił nieoczekiwany błąd. Wystąpił błąd aplikacji. Prosimy zalogować się ponownie. Jeśli problem będzie się powtarzał, prosimy o kontakt z serwisem."
            assertEquals(expected, e.message)
        }

        assertEquals("/Default/Account/LogOn", server.takeRequest().path)
    }

    @Test
    fun normalizeInvalidSymbol_digits() {
        server.enqueue("LoginPage-standard.html", LoginTest::class.java)
        server.enqueue("Logowanie-uonet.html", LoginTest::class.java)
        server.enqueue("Logowanie-brak-dostepu.html", LoginTest::class.java)
        server.enqueue("Offline.html", ErrorInterceptorTest::class.java)

        server.start(3000)

        try {
            runBlocking { getRegisterRepository("Default").getStudents() }
        } catch (e: Throwable) {
            assertTrue(e is VulcanException)
            val expected = "Wystąpił nieoczekiwany błąd. Wystąpił błąd aplikacji. Prosimy zalogować się ponownie. Jeśli problem będzie się powtarzał, prosimy o kontakt z serwisem."
            assertEquals(expected, e.message)
        }

        assertEquals("/Default/Account/LogOn", server.takeRequest().path)
        assertEquals(true, server.takeRequest().path?.startsWith("/Account/LogOn?ReturnUrl=%2FDefault"))
        assertEquals("/powiatwulkanowy/LoginEndpoint.aspx", server.takeRequest().path)
        assertEquals("/glubczyce2/LoginEndpoint.aspx", server.takeRequest().path)
    }
}

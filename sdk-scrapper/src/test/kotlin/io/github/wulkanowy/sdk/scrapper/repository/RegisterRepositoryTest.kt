package io.github.wulkanowy.sdk.scrapper.repository

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import io.github.wulkanowy.sdk.scrapper.Scrapper
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
            register = getService(
                service = RegisterService::class.java,
                url = "http://fakelog.localhost:3000/",
                okHttp = getOkHttp(errorInterceptor = false, autoLoginInterceptorOn = false)
            ),
            messages = getService(service = MessagesService::class.java, html = false),
            student = getService(service = StudentService::class.java, html = false),
            url = ServiceManager.UrlGenerator("http", "fakelog.localhost:3000", symbol, "")
        )
    }

    @Test
    fun normalLogin_one() {
        with(server) {
            enqueue("LoginPage-standard.html", LoginTest::class.java)
            enqueue("Logowanie-uonet.html", LoginTest::class.java)
            enqueue("Login-success.html", LoginTest::class.java)

            enqueue("JednostkiUzytkownika.json", MessagesTest::class.java)
            enqueue("LoginPage-standard.html", LoginTest::class.java)
            enqueue("WitrynaUcznia.html", RegisterTest::class.java)
            enqueue("UczenCache.json", RegisterTest::class.java)
            enqueue("UczenDziennik-no-semester.json", RegisterTest::class.java)

            repeat(4) { // 4x symbol
                enqueue("Logowanie-brak-dostepu.html", LoginTest::class.java)
            }
            start(3000)
        }

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
        with(server) {
            enqueue("LoginPage-standard.html", LoginTest::class.java)
            enqueue("Logowanie-uonet.html", LoginTest::class.java)
            enqueue("Login-success.html", LoginTest::class.java)

            enqueue("JednostkiUzytkownika.json", MessagesTest::class.java)
            enqueue("LoginPage-standard.html", LoginTest::class.java)
            enqueue("WitrynaUcznia.html", RegisterTest::class.java)
            enqueue("UczenCache.json", RegisterTest::class.java)
            enqueue("UczenDziennik.json", RegisterTest::class.java)

            repeat(4) { // 4x symbol
                enqueue("Logowanie-brak-dostepu.html", LoginTest::class.java)
            }
            start(3000)
        }

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
        with(server) {
            enqueue("LoginPage-standard.html", LoginTest::class.java)
            enqueue("Logowanie-uonet.html", LoginTest::class.java)
            enqueue("Login-success-triple.html", LoginTest::class.java)

            enqueue("JednostkiUzytkownika.json", MessagesTest::class.java)
            repeat(3) {
                enqueue("LoginPage-standard.html", LoginTest::class.java)
                enqueue("WitrynaUcznia.html", RegisterTest::class.java)
                enqueue("UczenCache.json", RegisterTest::class.java)
                enqueue("UczenDziennik-no-semester.json", RegisterTest::class.java)
            }

            repeat(4) { // 4x symbol
                enqueue("Logowanie-brak-dostepu.html", LoginTest::class.java)
            }
            start(3000)
        }

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
        with(server) {
            enqueue("LoginPage-standard.html", LoginTest::class.java)
            enqueue("Logowanie-uonet.html", LoginTest::class.java)
            enqueue("Login-success-triple.html", LoginTest::class.java)

            enqueue("JednostkiUzytkownika.json", MessagesTest::class.java)
            repeat(2) {
                enqueue("LoginPage-standard.html", LoginTest::class.java)
                enqueue("WitrynaUcznia.html", RegisterTest::class.java)
                enqueue("UczenCache.json", RegisterTest::class.java)
                enqueue("UczenDziennik-no-semester.json", RegisterTest::class.java)
            }

            enqueue("LoginPage-standard.html", LoginTest::class.java)
            enqueue("AplikacjaCzasowoWyłączona.html", ErrorInterceptorTest::class.java)

            repeat(4) { // 4x symbol
                enqueue("Logowanie-brak-dostepu.html", LoginTest::class.java)
            }
            start(3000)
        }

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

        val res = runCatching { runBlocking { normal.getStudents() } }
        assertEquals(
            "Wystąpił nieoczekiwany błąd. Wystąpił błąd aplikacji. Prosimy zalogować się ponownie. Jeśli problem będzie się powtarzał, prosimy o kontakt z serwisem.",
            res.exceptionOrNull()?.message
        )
        assertEquals(VulcanException::class.java, res.exceptionOrNull()!!::class.java)
    }

    @Test
    fun filterSymbolsWithSpaces() {
        with(server) {
            enqueue("LoginPage-standard.html", LoginTest::class.java)
            enqueue("Logowanie-uonet.html", LoginTest::class.java)

            repeat(5) {
                enqueue("Login-success-old.html", LoginTest::class.java)
                enqueue("JednostkiUzytkownika.json", MessagesTest::class.java)
            }

            start(3000)
        }

        val students = runBlocking { normal.getStudents() }
        assertEquals(0, students.size)
    }

    @Test
    fun normalizeInvalidSymbol_default() {
        with(server) {
            enqueue("LoginPage-standard.html", LoginTest::class.java)
            enqueue("Logowanie-uonet.html", LoginTest::class.java)
            enqueue("Logowanie-brak-dostepu.html", LoginTest::class.java)
            enqueue("Offline.html", ErrorInterceptorTest::class.java)

            start(3000)
        }

        val res = runCatching { runBlocking { getRegisterRepository("Default").getStudents() } }
        assertEquals(
            "Wystąpił nieoczekiwany błąd. Wystąpił błąd aplikacji. Prosimy zalogować się ponownie. Jeśli problem będzie się powtarzał, prosimy o kontakt z serwisem.",
            res.exceptionOrNull()?.message
        )
        assertTrue(res.exceptionOrNull() is VulcanException)
        assertEquals("/Default/Account/LogOn", server.takeRequest().path)
    }

    @Test
    fun normalizeInvalidSymbol_custom() {
        with(server) {
            enqueue("LoginPage-standard.html", LoginTest::class.java)
            enqueue("Logowanie-uonet.html", LoginTest::class.java)
            enqueue("Logowanie-brak-dostepu.html", LoginTest::class.java)
            enqueue("Offline.html", ErrorInterceptorTest::class.java)

            start(3000)
        }

        val res = runCatching { runBlocking { getRegisterRepository(" Rzeszów + ").getStudents() } }
        assertTrue(res.exceptionOrNull() is VulcanException)
        assertEquals(
            "Wystąpił nieoczekiwany błąd. Wystąpił błąd aplikacji. Prosimy zalogować się ponownie. Jeśli problem będzie się powtarzał, prosimy o kontakt z serwisem.",
            res.exceptionOrNull()?.message
        )
        assertEquals("/rzeszow/Account/LogOn", server.takeRequest().path)
    }

    @Test
    fun normalizeInvalidSymbol_trimMultipleSpaces() {
        with(server) {
            enqueue("LoginPage-standard.html", LoginTest::class.java)
            enqueue("Logowanie-uonet.html", LoginTest::class.java)
            enqueue("Logowanie-brak-dostepu.html", LoginTest::class.java)
            enqueue("Offline.html", ErrorInterceptorTest::class.java)

            start(3000)
        }

        val res = runCatching { runBlocking { getRegisterRepository(" Niepoprawny    symbol no ale + ").getStudents() } }
        assertTrue(res.exceptionOrNull() is VulcanException)
        assertEquals(
            "Wystąpił nieoczekiwany błąd. Wystąpił błąd aplikacji. Prosimy zalogować się ponownie. Jeśli problem będzie się powtarzał, prosimy o kontakt z serwisem.",
            res.exceptionOrNull()?.message
        )

        assertEquals("/niepoprawnysymbolnoale/Account/LogOn", server.takeRequest().path)
    }

    @Test
    fun normalizeInvalidSymbol_emptyFallback() {
        with(server) {
            enqueue("LoginPage-standard.html", LoginTest::class.java)
            enqueue("Logowanie-uonet.html", LoginTest::class.java)
            enqueue("Logowanie-brak-dostepu.html", LoginTest::class.java)
            enqueue("Offline.html", ErrorInterceptorTest::class.java)

            start(3000)
        }

        val res = runCatching { runBlocking { getRegisterRepository(" + ").getStudents() } }
        assertTrue(res.exceptionOrNull() is VulcanException)
        assertEquals(
            "Wystąpił nieoczekiwany błąd. Wystąpił błąd aplikacji. Prosimy zalogować się ponownie. Jeśli problem będzie się powtarzał, prosimy o kontakt z serwisem.",
            res.exceptionOrNull()?.message
        )
        assertEquals("/Default/Account/LogOn", server.takeRequest().path)
    }

    @Test
    fun normalizeInvalidSymbol_digits() {
        with(server) {
            enqueue("LoginPage-standard.html", LoginTest::class.java)
            enqueue("Logowanie-uonet.html", LoginTest::class.java)
            enqueue("Logowanie-brak-dostepu.html", LoginTest::class.java)
            enqueue("Offline.html", ErrorInterceptorTest::class.java)

            start(3000)
        }

        val res = runCatching { runBlocking { getRegisterRepository("Default").getStudents() } }
        assertTrue(res.exceptionOrNull() is VulcanException)
        assertEquals(
            "Wystąpił nieoczekiwany błąd. Wystąpił błąd aplikacji. Prosimy zalogować się ponownie. Jeśli problem będzie się powtarzał, prosimy o kontakt z serwisem.",
            res.exceptionOrNull()?.message
        )
        assertEquals("/Default/Account/LogOn", server.takeRequest().path)
        assertEquals(true, server.takeRequest().path?.startsWith("/Account/LogOn?ReturnUrl=%2FDefault"))
        assertEquals("/powiatwulkanowy/LoginEndpoint.aspx", server.takeRequest().path)
        assertEquals("/glubczyce2/LoginEndpoint.aspx", server.takeRequest().path)
    }
}

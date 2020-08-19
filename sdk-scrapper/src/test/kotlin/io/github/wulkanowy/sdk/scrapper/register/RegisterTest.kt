package io.github.wulkanowy.sdk.scrapper.register

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import io.github.wulkanowy.sdk.scrapper.Scrapper
import io.github.wulkanowy.sdk.scrapper.login.LoginHelper
import io.github.wulkanowy.sdk.scrapper.login.LoginTest
import io.github.wulkanowy.sdk.scrapper.messages.MessagesTest
import io.github.wulkanowy.sdk.scrapper.repository.RegisterRepository
import io.github.wulkanowy.sdk.scrapper.service.LoginService
import io.github.wulkanowy.sdk.scrapper.service.MessagesService
import io.github.wulkanowy.sdk.scrapper.service.RegisterService
import io.github.wulkanowy.sdk.scrapper.service.ServiceManager
import io.github.wulkanowy.sdk.scrapper.service.StudentService
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Test
import java.net.CookieManager

class RegisterTest : BaseLocalTest() {

    private val login by lazy {
        LoginHelper(Scrapper.LoginType.STANDARD, "http", "fakelog.localhost:3000", "default", CookieManager(),
            getService(LoginService::class.java, "http://fakelog.localhost:3000/", true, getOkHttp(true, false, Scrapper.LoginType.STANDARD)))
    }

    private val registerStudent by lazy {
        RegisterRepository("default", "jan@fakelog.localhost", "jan123", login,
            getService(RegisterService::class.java, "http://fakelog.localhost:3000/Default/", true, getOkHttp(false, false)),
            getService(MessagesService::class.java, "http://fakelog.localhost:3000", false),
            getService(StudentService::class.java, "http://fakelog.localhost:3000", false),
            ServiceManager.UrlGenerator("http", "fakelog.localhost:3000", "default", "123")
        )
    }

    @Test
    fun filterStudentsByClass() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("LoginPage-standard.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Login-success.html").readText()))
        server.enqueue("JednostkiUzytkownika.json", MessagesTest::class.java)
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("LoginPage-standard.html").readText()))
        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("WitrynaUcznia.html").readText()))
        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("UczenCache.json").readText()))
        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("UczenDziennik-multi.json").readText()))
        (0..5).onEach { // 5x symbol
            server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        }

        server.start(3000)

        val res = runBlocking { registerStudent.getStudents() }

        assertEquals(2, res.size)

        res[0].run {
            assertEquals(3881, studentId)
            assertEquals("Jan", studentName)
            assertEquals("Kowalski", studentSurname)
            assertEquals(121, classId)
            assertEquals("Publiczna szkoła Wulkanowego nr 1 w fakelog.cf", schoolName)
            assertEquals("Te", className)
        }

        res[1].run {
            assertEquals(3881, studentId)
            assertEquals("Jan", studentName)
            assertEquals("Kowalski", studentSurname)
            assertEquals(119, classId)
            assertEquals("Publiczna szkoła Wulkanowego nr 1 w fakelog.cf", schoolName)
            assertEquals("Ti", className)
        }
    }

    @Test
    fun getStudents_filterDiariesWithoutSemester() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("LoginPage-standard.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Login-success.html").readText()))
        server.enqueue("JednostkiUzytkownika.json", MessagesTest::class.java)
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("LoginPage-standard.html").readText()))
        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("WitrynaUcznia.html").readText()))
        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("UczenCache.json").readText()))
        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("UczenDziennik-no-semester.json").readText()))
        (0..5).onEach { // 5x symbol
            server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        }

        server.start(3000)

        val res = runBlocking { registerStudent.getStudents() }

        assertEquals(1, res.size)

        res[0].run {
            assertEquals(1, studentId)
            assertEquals("Jan", studentName)
            assertEquals("Kowalski", studentSurname)
            assertEquals(1, classId)
            assertEquals("Publiczna szkoła Wulkanowego nr 1 w fakelog.cf", schoolName)
        }
    }

    @Test
    fun getStudents_filterDiariesWithEmptySemester() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("LoginPage-standard.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Login-success.html").readText()))
        server.enqueue("JednostkiUzytkownika.json", MessagesTest::class.java)
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("LoginPage-standard.html").readText()))
        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("WitrynaUcznia.html").readText()))
        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("UczenCache.json").readText()))
        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("UczenDziennik-empty-semester.json").readText()))
        (0..5).onEach { // 5x symbol
            server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        }

        server.start(3000)

        val res = runBlocking { registerStudent.getStudents() }

        assertEquals(1, res.size)

        res[0].run {
            assertEquals(1, studentId)
            assertEquals("Jan", studentName)
            assertEquals("Kowalski", studentSurname)
            assertEquals(1, classId)
            assertEquals("Publiczna szkoła Wulkanowego nr 1 w fakelog.cf", schoolName)
            assertEquals("A", className)
        }
    }

    @Test
    fun getStudents_classNameOrder() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("LoginPage-standard.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Login-success.html").readText()))
        server.enqueue("JednostkiUzytkownika.json", MessagesTest::class.java)
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("LoginPage-standard.html").readText()))
        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("WitrynaUcznia.html").readText()))
        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("UczenCache.json").readText()))
        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("UczenDziennik.json").readText()))
        (0..5).onEach { // 5x symbol
            server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        }

        server.start(3000)

        val res = runBlocking { registerStudent.getStudents() }

        assertEquals(2, res.size)

        res[0].run {
            assertEquals(1, studentId)
            assertEquals("Jan", studentName)
            assertEquals("Kowalski", studentSurname)
            assertEquals(1, classId)
            assertEquals("A", className)
            assertEquals("Publiczna szkoła Wulkanowego nr 1 w fakelog.cf", schoolName)
        }

        res[1].run {
            assertEquals(2, studentId)
            assertEquals("Joanna", studentName)
            assertEquals("Czerwińska", studentSurname)
            assertEquals(2, classId)
            assertEquals("A", className)
            assertEquals("Publiczna szkoła Wulkanowego nr 1 w fakelog.cf", schoolName)
        }
    }
}

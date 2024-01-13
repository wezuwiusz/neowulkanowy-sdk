package io.github.wulkanowy.sdk.scrapper.interceptor

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import io.github.wulkanowy.sdk.scrapper.CookieJarCabinet
import io.github.wulkanowy.sdk.scrapper.Scrapper
import io.github.wulkanowy.sdk.scrapper.login.LoginHelper
import io.github.wulkanowy.sdk.scrapper.login.LoginTest
import io.github.wulkanowy.sdk.scrapper.login.UrlGenerator
import io.github.wulkanowy.sdk.scrapper.notes.NotesTest
import io.github.wulkanowy.sdk.scrapper.register.HomePageResponse
import io.github.wulkanowy.sdk.scrapper.register.RegisterTest
import io.github.wulkanowy.sdk.scrapper.service.LoginService
import io.github.wulkanowy.sdk.scrapper.service.StudentService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.jsoup.nodes.Document
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.URL

class AutoLoginInterceptorTest : BaseLocalTest() {

    private lateinit var loginService: LoginService

    private lateinit var loginHelper: LoginHelper

    @Test
    fun oneLoginAtTime() = runBlocking {
        with(server) {
            enqueue("unknown-error.txt", RegisterTest::class.java)
            enqueue("Logowanie-uonet.html", LoginTest::class.java)
            enqueue("Login-success.html", LoginTest::class.java)
            enqueue("UwagiIOsiagniecia.json", NotesTest::class.java)
            start(3000)
        }
        init()

        val service = getService { loginHelper.login("", "") }
        val notes = service.getNotes()

        assertEquals(3, notes.data?.notes?.size)
    }

    @Test
    fun oneLoginAtTimeWithError() = runBlocking {
        with(server) {
            enqueue("unknown-error.txt", RegisterTest::class.java)
            enqueue("Logowanie-uonet.html", LoginTest::class.java)
            enqueue("Offline.html", AutoLoginInterceptor::class.java)
            enqueue("unknown-error.txt", RegisterTest::class.java)
            start(3000)
        }
        init()

        val service = getService { loginHelper.login("", "") }
        val result = runCatching { service.getNotes() }

        assertEquals(true, result.isFailure)
        assertEquals(true, result.exceptionOrNull()?.message?.startsWith("Wystąpił nieoczekiwany błąd"))
    }

    // @Test
    fun simultaneousLogin() = runBlocking {
        with(server) {
            repeat(3) {
                enqueue("unknown-error.txt", RegisterTest::class.java)
            }

            enqueue("Logowanie-uonet.html", LoginTest::class.java)
            enqueue("Login-success.html", LoginTest::class.java)

            repeat(3) {
                enqueue("UwagiIOsiagniecia.json", NotesTest::class.java)
            }

            start(3000)
        }

        init()
        val service = getService { loginHelper.login("", "") }

        val notes1 = async { service.getNotes() }
        val notes2 = async { service.getNotes() }
        val notes3 = async { service.getNotes() }

        val a = awaitAll(notes1, notes2, notes3)

        assertEquals((1 + 1 + 1), a.size)
    }

    // @Test
    fun simultaneousLoginWithError() = runBlocking {
        with(server) {
            repeat(3) {
                enqueue("unknown-error.txt", RegisterTest::class.java)
            }

            enqueue("Logowanie-uonet.html", LoginTest::class.java)
            enqueue("Offline.html", AutoLoginInterceptor::class.java)

            repeat(3) {
                enqueue("unknown-error.txt", RegisterTest::class.java)
            }

            start(3000)
        }

        init()

        val service = getService { loginHelper.login("", "") }
        supervisorScope {
            val notes1 = async { service.getNotes() }
            val notes2 = async { service.getNotes() }
            val notes3 = async { service.getNotes() }

            val result = runCatching { awaitAll(notes1, notes2, notes3) }
            assertEquals(true, result.isFailure)
            assertTrue(result.exceptionOrNull()?.message?.startsWith("Wystąpił nieoczekiwany błąd") == true)
        }
    }

    private fun init() {
        loginService = getService(LoginService::class.java)
        loginHelper = LoginHelper(
            loginType = Scrapper.LoginType.STANDARD,
            schema = "http",
            host = "${server.hostName}:${server.port}",
            domainSuffix = "",
            symbol = "powiatwulkanowy",
            cookieJarCabinet = CookieJarCabinet(),
            api = loginService,
            urlGenerator = UrlGenerator(URL("http://localhost/"), "", "lodz", ""),
        )
    }

    private fun getService(checkJar: Boolean = false, notLoggedInCallback: suspend () -> HomePageResponse): StudentService {
        val interceptor = AutoLoginInterceptor(
            loginType = Scrapper.LoginType.STANDARD,
            cookieJarCabinet = CookieJarCabinet(),
            emptyCookieJarIntercept = checkJar,
            notLoggedInCallback = notLoggedInCallback,
            fetchStudentCookies = { "http://localhost".toHttpUrl() to Document("") },
            fetchMessagesCookies = { "http://localhost".toHttpUrl() to Document("") },
        )
        val okHttp = getOkHttp(autoLogin = true, autoLoginInterceptorOn = true, autoLoginInterceptor = interceptor)
        return getService(StudentService::class.java, html = false, okHttp = okHttp)
    }
}

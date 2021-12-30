package io.github.wulkanowy.sdk.scrapper.interceptor

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import io.github.wulkanowy.sdk.scrapper.MainCoroutineRule
import io.github.wulkanowy.sdk.scrapper.Scrapper
import io.github.wulkanowy.sdk.scrapper.login.LoginHelper
import io.github.wulkanowy.sdk.scrapper.login.LoginTest
import io.github.wulkanowy.sdk.scrapper.notes.NotesTest
import io.github.wulkanowy.sdk.scrapper.register.RegisterTest
import io.github.wulkanowy.sdk.scrapper.service.LoginService
import io.github.wulkanowy.sdk.scrapper.service.StudentService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.net.CookieManager

class AutoLoginInterceptorTest : BaseLocalTest() {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    lateinit var loginService: LoginService

    lateinit var loginHelper: LoginHelper

    @Before
    fun setUp() {
    }

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
            symbol = "powiatwulkanowy",
            cookies = CookieManager(),
            api = loginService
        )
    }

    private fun getService(checkJar: Boolean = false, notLoggedInCallback: suspend () -> Unit): StudentService {
        val interceptor = AutoLoginInterceptor(
            loginType = Scrapper.LoginType.STANDARD,
            jar = CookieManager(),
            emptyCookieJarIntercept = checkJar,
            notLoggedInCallback = notLoggedInCallback
        )
        val okHttp = getOkHttp(autoLogin = true, autoLoginInterceptorOn = true, autoLoginInterceptor = interceptor)
        return getService(StudentService::class.java, html = false, okHttp = okHttp)
    }
}

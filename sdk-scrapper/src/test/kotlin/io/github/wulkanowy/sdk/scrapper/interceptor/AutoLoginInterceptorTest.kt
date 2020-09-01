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
import org.junit.Assert.assertEquals
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
        server.enqueue("unknown-error.txt", RegisterTest::class.java)
        server.enqueue("Logowanie-uonet.html", LoginTest::class.java)
        server.enqueue("Login-success.html", LoginTest::class.java)
        server.enqueue("UwagiIOsiagniecia.json", NotesTest::class.java)
        server.start(3000)
        init()

        val service = getService { runBlocking { loginHelper.login("", "").studentSchools.isNotEmpty() } }
        val notes = service.getNotes()

        assertEquals(3, notes.data?.notes?.size)
    }

    @Test
    fun simultaneousLogin() = runBlocking {
        server.enqueue("unknown-error.txt", RegisterTest::class.java)
        server.enqueue("unknown-error.txt", RegisterTest::class.java)

        server.enqueue("Logowanie-uonet.html", LoginTest::class.java)
        server.enqueue("Login-success.html", LoginTest::class.java)
        server.enqueue("UwagiIOsiagniecia.json", NotesTest::class.java)

        server.enqueue("Logowanie-uonet.html", LoginTest::class.java)
        server.enqueue("Login-success.html", LoginTest::class.java)
        server.enqueue("UwagiIOsiagniecia.json", NotesTest::class.java)
        server.start(3000)
        init()
        val service = getService { runBlocking { loginHelper.login("", "").studentSchools.isNotEmpty() } }

        val notes1 = async { service.getNotes() }
        val notes2 = async { service.getNotes() }

        val a = awaitAll(notes1, notes2)

        assertEquals(1 + 1, a.size)
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

    private fun getService(checkJar: Boolean = false, notLoggedInCallback: () -> Boolean): StudentService {
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

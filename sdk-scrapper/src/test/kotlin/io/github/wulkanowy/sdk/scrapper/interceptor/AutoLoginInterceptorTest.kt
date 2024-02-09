package io.github.wulkanowy.sdk.scrapper.interceptor

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import io.github.wulkanowy.sdk.scrapper.CookieJarCabinet
import io.github.wulkanowy.sdk.scrapper.Scrapper
import io.github.wulkanowy.sdk.scrapper.login.LoginHelper
import io.github.wulkanowy.sdk.scrapper.login.LoginTest
import io.github.wulkanowy.sdk.scrapper.login.UrlGenerator
import io.github.wulkanowy.sdk.scrapper.messages.MessagesTest
import io.github.wulkanowy.sdk.scrapper.notes.NotesTest
import io.github.wulkanowy.sdk.scrapper.register.HomePageResponse
import io.github.wulkanowy.sdk.scrapper.register.RegisterTest
import io.github.wulkanowy.sdk.scrapper.service.LoginService
import io.github.wulkanowy.sdk.scrapper.service.StudentService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.test.runTest
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.jsoup.Jsoup
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

    @Test
    fun checkAppendedModuleHeaders() = runTest {
        with(server) {
            enqueue("unknown-error.txt", RegisterTest::class.java)
            enqueue("Logowanie-uonet.html", LoginTest::class.java)
            enqueue("Login-success.html", LoginTest::class.java)
            // enqueue("Start.html", MessagesTest::class.java)
            // enqueue("WitrynaUcznia.html", RegisterTest::class.java)
            enqueue("UczenCache.json", RegisterTest::class.java)
            start(3000)
        }
        init()

        val studentService = getService(
            fetchModuleCookies = { site ->
                val html = when (site) {
                    UrlGenerator.Site.STUDENT -> RegisterTest::class.java.getResource("WitrynaUcznia.html")!!.readText()
                    UrlGenerator.Site.MESSAGES -> MessagesTest::class.java.getResource("Start.html")!!.readText()
                    else -> error("Not supported here")
                }
                val subdomain = when (site) {
                    UrlGenerator.Site.STUDENT -> "uczen"
                    UrlGenerator.Site.MESSAGES -> "wiadomosciplus"
                    else -> error("Not supported here")
                }
                "https://uonetplus-${subdomain}.localhost".toHttpUrl() to Jsoup.parse(html)
            },
            notLoggedInCallback = { loginHelper.login("", "") },
        )
        studentService.getUserCache()

        repeat(3) { server.takeRequest() }
        val retriedRequest = server.takeRequest()
        assertEquals(
            "7SaCmj247xiKA4nQcTqLJ8J56UnZpxL3zLNENZjKAdFQN3xN26EwRdhAezyo5Wx3P2iWVPLTc3fpjPCNMbEPLmxF4RrLeaAGdQevu8pgbEB2TocqfBPjWzNLyHXBcqxKM",
            retriedRequest.getHeader("X-V-RequestVerificationToken"),
        )
        assertEquals("2w68d2SFGnvRtVhuXoLYdxL3ue4F9yqD", retriedRequest.getHeader("X-V-AppGuid"))
        assertEquals("18.07.0003.31856", retriedRequest.getHeader("X-V-AppVersion"))
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
            urlGenerator = UrlGenerator(URL("http://localhost/"), "", "lodz", "test"),
        )
    }

    private fun getService(
        checkJar: Boolean = false,
        fetchModuleCookies: (UrlGenerator.Site) -> Pair<HttpUrl, Document> = { "http://localhost".toHttpUrl() to Document("") },
        notLoggedInCallback: suspend () -> HomePageResponse,
    ): StudentService {
        val urlGenerator = UrlGenerator(URL("http://uonetplus-uczen.localhost/"), "", "lodz", "")
        val interceptor = AutoLoginInterceptor(
            loginType = Scrapper.LoginType.STANDARD,
            cookieJarCabinet = CookieJarCabinet(),
            emptyCookieJarIntercept = checkJar,
            notLoggedInCallback = notLoggedInCallback,
            fetchModuleCookies = fetchModuleCookies,
            urlGenerator = urlGenerator,
        )
        val okHttp = getOkHttp(autoLogin = true, autoLoginInterceptorOn = true, autoLoginInterceptor = interceptor)
        return getService(
            service = StudentService::class.java,
            url = server.url("/").newBuilder()
                .host("uonetplus-uczen.fakelog.localhost")
                .build().toString(),
            html = false,
            okHttp = okHttp,
        )
    }
}

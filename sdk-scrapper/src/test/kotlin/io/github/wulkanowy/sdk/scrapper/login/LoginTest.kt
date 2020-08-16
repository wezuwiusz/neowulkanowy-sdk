package io.github.wulkanowy.sdk.scrapper.login

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import io.github.wulkanowy.sdk.scrapper.Scrapper
import io.github.wulkanowy.sdk.scrapper.exception.VulcanException
import io.github.wulkanowy.sdk.scrapper.homework.HomeworkTest
import io.github.wulkanowy.sdk.scrapper.interceptor.ErrorInterceptorTest
import io.github.wulkanowy.sdk.scrapper.service.LoginService
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.CookieManager

class LoginTest : BaseLocalTest() {

    private val normal by lazy {
        LoginHelper(
            loginType = Scrapper.LoginType.STANDARD,
            schema = "http",
            host = "fakelog.localhost:3000",
            symbol = "default",
            cookies = CookieManager(),
            api = getService(LoginService::class.java, "http://fakelog.localhost:3000/")
        )
    }

    private val adfs by lazy {
        LoginHelper(
            loginType = Scrapper.LoginType.ADFSCards,
            schema = "http",
            host = "fakelog.localhost:3000",
            symbol = "default",
            cookies = CookieManager(),
            api = getService(LoginService::class.java, "http://fakelog.localhost:3000/", true, getOkHttp(true, false, Scrapper.LoginType.ADFSCards))
        )
    }

    @Test
    fun adfsTest() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("ADFS-form-1.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("ADFS-form-2.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-cufs.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Login-success.html").readText()))
        server.start(3000)

        val res = runBlocking { adfs.login("jan@fakelog.cf", "jan123") }

        assertTrue(res.studentSchools.isNotEmpty())
    }

    @Test
    fun normalLogin() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Login-success.html").readText()))
        server.start(3000)

        val res = runBlocking { normal.login("jan@fakelog.cf", "jan123") }

        assertTrue(res.studentSchools.isNotEmpty())
    }

    @Test
    fun multiLogin() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Login-success.html").readText()))
        server.start(3000)

        runBlocking { normal.login("jan@fakelog.cf", "jan123") }

        assertEquals("[text=LoginName=jan%40fakelog.cf&Password=jan123]", server.takeRequest().body.toString())
    }

    @Test
    fun multiLogin_withLogin() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Login-success-account-switch.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Login-success-account-switch.html").readText()))
        server.start(3000)

        runBlocking { normal.login("jan||jan@fakelog.cf", "jan123") }

        assertEquals("[text=LoginName=jan&Password=jan123]", server.takeRequest().body.toString())
    }

    @Test
    fun normalLogin_encodingError() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet-encoding-error.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Login-success.html").readText()))
        server.start(3000)

        runBlocking { normal.login("jan@fakelog.cf", "jan123") }

        server.takeRequest()
        assertFalse(server.takeRequest().body.readUtf8().contains("ValueType%3D%26t%3Bhttp")) // ValueType=&t;http
    }

    @Test
    fun adfsBadCredentialsException() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("ADFS-form-1.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("ADFS-form-2.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-adfs-zle-haslo.html").readText()))
        server.start(3000)

        try {
            runBlocking { adfs.login("jan@fakelog.cf", "jan1234") }
        } catch (e: Throwable) {
            assertTrue(e is BadCredentialsException)
        }
    }

    @Test
    fun normalBadCredentialsException() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-normal-zle-haslo.html").readText()))
        server.start(3000)

        try {
            runBlocking { adfs.login("jan@fakelog.cf", "jan1234") }
        } catch (e: Throwable) {
            assertTrue(e is BadCredentialsException)
        }
    }

    @Test
    fun accessPermissionException() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.start(3000)

        try {
            runBlocking { adfs.login("jan@fakelog.cf", "jan1234") }
        } catch (e: Throwable) {
            assertTrue(e is AccountPermissionException)
            assertEquals("Adres nie został zarejestrowany w dzienniku uczniowskim jako adres rodzica, bądź ucznia.", e.message)
        }
    }

    @Test
    fun alreadyLoggedIn() {
        server.enqueue(MockResponse().setBody(HomeworkTest::class.java.getResource("ZadaniaDomowe.html").readText()))
        server.start(3000)

        runBlocking { normal.login("jan@fakelog.cf", "jan123") }
    }

    @Test
    fun invalidCertificatePage() {
        server.enqueue(MockResponse().setBody(ErrorInterceptorTest::class.java.getResource("Offline.html").readText()))
        server.start(3000)

        try {
            runBlocking { adfs.login("jan@fakelog.cf", "jan1234") }
        } catch (e: Throwable) {
            assertTrue(e is VulcanException)
        }
    }
}

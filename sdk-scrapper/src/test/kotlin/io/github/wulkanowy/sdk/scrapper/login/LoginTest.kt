package io.github.wulkanowy.sdk.scrapper.login

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import io.github.wulkanowy.sdk.scrapper.Scrapper
import io.github.wulkanowy.sdk.scrapper.exception.AccountInactiveException
import io.github.wulkanowy.sdk.scrapper.exception.VulcanException
import io.github.wulkanowy.sdk.scrapper.interceptor.ErrorInterceptorTest
import io.github.wulkanowy.sdk.scrapper.service.LoginService
import kotlinx.coroutines.runBlocking
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
            loginType = Scrapper.LoginType.ADFS,
            schema = "http",
            host = "fakelog.localhost:3000",
            symbol = "default",
            cookies = CookieManager(),
            api = getService(
                service = LoginService::class.java,
                url = "http://fakelog.localhost:3000/",
                html = true,
                okHttp = getOkHttp(
                    errorInterceptor = true,
                    autoLoginInterceptorOn = false,
                    loginType = Scrapper.LoginType.ADFS,
                ),
            ),
        )
    }

    @Test
    fun adfsTest() {
        with(server) {
            enqueue("Logowanie-cufs.html")
            enqueue("Logowanie-uonet.html")
            enqueue("Login-success.html")
            start(3000)
        }

        val res = runBlocking { adfs.login("jan@fakelog.cf", "jan123") }

        assertTrue(res.studentSchools.isNotEmpty())
    }

    @Test
    fun normalLogin() {
        with(server) {
            enqueue("Logowanie-uonet.html")
            enqueue("Login-success.html")
            start(3000)
        }

        val res = runBlocking { normal.login("jan@fakelog.cf", "jan123") }

        assertTrue(res.studentSchools.isNotEmpty())
    }

    @Test
    fun multiLogin() {
        with(server) {
            enqueue("Logowanie-uonet.html")
            enqueue("Login-success.html")
            start(3000)
        }

        runBlocking { normal.login("jan@fakelog.cf", "jan123") }

        assertEquals("[text=LoginName=jan%40fakelog.cf&Password=jan123]", server.takeRequest().body.toString())
    }

    @Test
    fun multiLogin_withLogin() {
        with(server) {
            enqueue("Logowanie-uonet.html")
            enqueue("Login-success-account-switch.html")
            enqueue("Login-success-account-switch.html")
            start(3000)
        }

        runBlocking { normal.login("jan||jan@fakelog.cf", "jan123") }

        assertEquals("[text=LoginName=jan&Password=jan123]", server.takeRequest().body.toString())
    }

    @Test
    fun normalLogin_encodingError() {
        with(server) {
            enqueue("Logowanie-uonet-encoding-error.html")
            enqueue("Login-success.html")
            start(3000)
        }

        runBlocking { normal.login("jan@fakelog.cf", "jan123") }

        server.takeRequest()
        assertFalse(server.takeRequest().body.readUtf8().contains("ValueType%3D%26t%3Bhttp")) // ValueType=&t;http
    }

    @Test
    fun accessAccountInactiveException() {
        with(server) {
            enqueue("Logowanie-uonet.html")
            enqueue("Logowanie-nieaktywne.html")
            start(3000)
        }

        try {
            runBlocking { normal.login("jan@fakelog.cf", "jan1234") }
        } catch (e: Throwable) {
            assertEquals(AccountInactiveException::class, e::class)
            assertEquals("Login i hasło użytkownika są poprawne, ale konto nie jest aktywne w żadnej jednostce sprawozdawczej", e.message)
        }
    }

    @Test
    fun adfsBadCredentialsException() {
        with(server) {
            enqueue("Logowanie-adfs-zle-haslo.html")
            start(3000)
        }

        try {
            runBlocking { adfs.login("jan@fakelog.cf", "jan1234") }
        } catch (e: Throwable) {
            assertTrue(e is BadCredentialsException)
        }
    }

    @Test
    fun normalBadCredentialsException() {
        with(server) {
            enqueue("Logowanie-uonet.html")
            enqueue("Logowanie-normal-zle-haslo.html")
            start(3000)
        }

        try {
            runBlocking { adfs.login("jan@fakelog.cf", "jan1234") }
        } catch (e: Throwable) {
            assertTrue(e is BadCredentialsException)
        }
    }

    @Test
    fun accessPermissionException() {
        with(server) {
            enqueue("Logowanie-uonet.html")
            enqueue("Logowanie-brak-dostepu.html")
            start(3000)
        }

        try {
            runBlocking { adfs.login("jan@fakelog.cf", "jan1234") }
        } catch (e: Throwable) {
            assertTrue(e is AccountPermissionException)
            assertEquals("Adres nie został zarejestrowany w dzienniku uczniowskim jako adres rodzica, bądź ucznia.", e.message)
        }
    }

    @Test
    fun invalidCertificatePage() {
        with(server) {
            enqueue("Offline.html", ErrorInterceptorTest::class.java)
            start(3000)
        }

        try {
            runBlocking { adfs.login("jan@fakelog.cf", "jan1234") }
        } catch (e: Throwable) {
            assertTrue(e is VulcanException)
        }
    }
}

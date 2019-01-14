package io.github.wulkanowy.api.login

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.api.BaseLocalTest
import io.github.wulkanowy.api.homework.HomeworkTest
import io.github.wulkanowy.api.interceptor.ErrorInterceptorTest
import io.github.wulkanowy.api.interceptor.VulcanException
import io.github.wulkanowy.api.register.SendCertificateResponse
import io.github.wulkanowy.api.repository.LoginRepository
import io.github.wulkanowy.api.service.LoginService
import io.reactivex.observers.TestObserver
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.CookieManager

class LoginTest : BaseLocalTest() {

    private val normal by lazy {
        LoginRepository(Api.LoginType.STANDARD, "http", "fakelog.localhost:3000", "default", CookieManager(),
                getService(LoginService::class.java, "http://fakelog.localhost:3000/"))
    }

    private val adfs by lazy {
        LoginRepository(Api.LoginType.ADFSCards, "http", "fakelog.localhost:3000", "default", CookieManager(),
                getService(LoginService::class.java, "http://fakelog.localhost:3000/", true, true, false, Api.LoginType.ADFSCards))
    }

    @Test
    fun adfsTest() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("ADFS-form-1.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("ADFS-form-2.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-cufs.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Login-success.html").readText()))
        server.start(3000)

        val res = adfs.login("jan@fakelog.cf", "jan123").blockingGet()

        assertTrue(res.oldStudentSchools.isNotEmpty())
    }

    @Test
    fun normalLogin() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Login-success.html").readText()))
        server.start(3000)

        val res = normal.login("jan@fakelog.cf", "jan123").blockingGet()

        assertTrue(res.oldStudentSchools.isNotEmpty())
    }

    @Test
    fun normalLogin_beforeNewStudentSite() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Login-success-old.html").readText()))
        server.start(3000)

        val res = normal.login("jan@fakelog.cf", "jan123").blockingGet()

        assertTrue(res.oldStudentSchools.isNotEmpty())
    }

    @Test
    fun normalLogin_encodingError() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet-encoding-error.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Login-success.html").readText()))
        server.start(3000)

        normal.login("jan@fakelog.cf", "jan123").blockingGet()

        server.takeRequest()
        assertFalse(server.takeRequest().body.readUtf8().contains("ValueType%3D%26t%3Bhttp")) // ValueType=&t;http
    }

    @Test
    fun adfsBadCredentialsException() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("ADFS-form-1.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("ADFS-form-2.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-adfs-zle-haslo.html").readText()))
        server.start(3000)

        val res = adfs.login("jan@fakelog.cf", "jan1234")
        val observer = TestObserver<SendCertificateResponse>()
        res.subscribe(observer)
        observer.assertTerminated()
        observer.assertError(BadCredentialsException::class.java)
    }

    @Test
    fun normalBadCredentialsException() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-normal-zle-haslo.html").readText()))
        server.start(3000)

        val res = normal.login("jan@fakelog.cf", "jan1234")
        val observer = TestObserver<SendCertificateResponse>()
        res.subscribe(observer)
        observer.assertTerminated()
        observer.assertError(BadCredentialsException::class.java)
    }

    @Test
    fun accessPermissionException() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.start(3000)

        val res = normal.login("jan@fakelog.cf", "jan123")
        val observer = TestObserver<SendCertificateResponse>()
        res.subscribe(observer)
        observer.assertTerminated()
        observer.assertError(AccountPermissionException::class.java)
        observer.assertError {
            it.localizedMessage == "Adres nie został zarejestrowany w dzienniku uczniowskim jako adres rodzica, bądź ucznia."
        }
    }

    @Test
    fun alreadyLoggedIn() {
        server.enqueue(MockResponse().setBody(HomeworkTest::class.java.getResource("ZadaniaDomowe.html").readText()))
        server.start(3000)

        val res = normal.login("jan@fakelog.cf", "jan123")
        val observer = TestObserver<SendCertificateResponse>()
        res.subscribe(observer)
        observer.assertComplete()
    }

    @Test
    fun invalidCertificatePage() {
        server.enqueue(MockResponse().setBody(ErrorInterceptorTest::class.java.getResource("Offline.html").readText()))
        server.start(3000)

        val res = normal.login("jan@fakelog.cf", "jan123")
        val observer = TestObserver<SendCertificateResponse>()
        res.subscribe(observer)
        observer.assertTerminated()
        observer.assertError(VulcanException::class.java)
    }
}

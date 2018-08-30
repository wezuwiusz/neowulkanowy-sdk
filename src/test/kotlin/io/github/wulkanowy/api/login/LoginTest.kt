package io.github.wulkanowy.api.login

import io.github.wulkanowy.api.BaseTest
import io.github.wulkanowy.api.auth.AccountPermissionException
import io.github.wulkanowy.api.auth.BadCredentialsException
import io.github.wulkanowy.api.register.HomepageResponse
import io.github.wulkanowy.api.repository.LoginRepository
import io.github.wulkanowy.api.service.LoginService
import io.reactivex.observers.TestObserver
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LoginTest : BaseTest() {

    private lateinit var server: MockWebServer

    @Before
    fun setUp() {
        server = MockWebServer()
    }

    private fun getRepo(url: String): LoginRepository {
        return LoginRepository("http", url.removePrefix("http://").removeSuffix("/"), "default",
                getService(LoginService::class.java, url))
    }

    @Test
    fun adfsTest() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("ADFS-form-1.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("ADFS-form-2.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-cufs.html").readText().replace("3000", "3001")))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText().replace("3000", "3001")))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Login-success.html").readText()))
        server.start(3001)

        val res = getRepo(server.url("/").toString()).login("jan@fakelog.cf", "jan123").blockingGet()

        assertTrue(res.schools.isNotEmpty())
    }

    @Test
    fun normalLogin() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Login-success.html").readText()))
        server.start(3000)

        val res = getRepo(server.url("/").toString()).login("jan@fakelog.cf", "jan123").blockingGet()

        assertTrue(res.schools.isNotEmpty())
    }

    @Test
    fun adfsBadCredentialsException() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("ADFS-form-1.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("ADFS-form-2.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-adfs-zle-haslo.html").readText()))
        server.start(3001)

        val res = getRepo(server.url("/").toString()).login("jan@fakelog.cf", "jan1234")
        val observer = TestObserver<HomepageResponse>()
        res.subscribe(observer)
        observer.assertTerminated()
        observer.assertError(BadCredentialsException::class.java)
    }

    @Test
    fun normalBadCredentialsException() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-normal-zle-haslo.html").readText()))
        server.start(3000)

        val res = getRepo(server.url("/").toString()).login("jan@fakelog.cf", "jan1234")
        val observer = TestObserver<HomepageResponse>()
        res.subscribe(observer)
        observer.assertTerminated()
        observer.assertError(BadCredentialsException::class.java)
    }

    @Test
    fun accessPermissionException() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.start(3000)

        val res = getRepo(server.url("/").toString()).login("jan@fakelog.cf", "jan123")
        val observer = TestObserver<HomepageResponse>()
        res.subscribe(observer)
        observer.assertTerminated()
        observer.assertError(AccountPermissionException::class.java)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }
}

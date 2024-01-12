package io.github.wulkanowy.sdk.scrapper.register

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import io.github.wulkanowy.sdk.scrapper.CookieJarCabinet
import io.github.wulkanowy.sdk.scrapper.Scrapper
import io.github.wulkanowy.sdk.scrapper.login.LoginHelper
import io.github.wulkanowy.sdk.scrapper.login.LoginTest
import io.github.wulkanowy.sdk.scrapper.login.UrlGenerator
import io.github.wulkanowy.sdk.scrapper.repository.RegisterRepository
import io.github.wulkanowy.sdk.scrapper.service.LoginService
import io.github.wulkanowy.sdk.scrapper.service.RegisterService
import io.github.wulkanowy.sdk.scrapper.service.StudentService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.net.URL

class RegisterTest : BaseLocalTest() {

    private val login by lazy {
        LoginHelper(
            loginType = Scrapper.LoginType.STANDARD,
            schema = "http",
            host = "fakelog.localhost:3000",
            domainSuffix = "",
            symbol = "default",
            cookieJarCabinet = CookieJarCabinet(),
            api = getService(
                service = LoginService::class.java,
                url = "http://fakelog.localhost:3000/",
                html = true,
                okHttp = getOkHttp(
                    errorInterceptor = true,
                    autoLoginInterceptorOn = false,
                    loginType = Scrapper.LoginType.STANDARD,
                ),
            ),
            urlGenerator = UrlGenerator(URL("http://localhost/"), "", "lodz", ""),
        )
    }

    private val registerStudent by lazy {
        RegisterRepository(
            startSymbol = "default",
            email = "jan@fakelog.localhost",
            password = "jan123",
            loginHelper = login,
            register = getService(
                service = RegisterService::class.java,
                url = "http://fakelog.localhost:3000/Default/",
                html = true,
                okHttp = getOkHttp(
                    errorInterceptor = false,
                    autoLoginInterceptorOn = false,
                ),
            ),
            student = getService(StudentService::class.java, "http://fakelog.localhost:3000", false),
            url = UrlGenerator(
                schema = "http",
                host = "fakelog.localhost:3000",
                domainSuffix = "",
                symbol = "default",
                schoolId = "123",
            ),
        )
    }

    @Test
    fun filterStudentsByClass() = runTest {
        with(server) {
            enqueue("LoginPage-standard.html", LoginTest::class.java)
            enqueue("Logowanie-uonet.html", LoginTest::class.java)
            enqueue("Logowanie-uonet.html", LoginTest::class.java)
            enqueue("Login-success.html", LoginTest::class.java)
            enqueue("WitrynaUcznia.html", RegisterTest::class.java)
            enqueue("UczenCache.json", RegisterTest::class.java)
            enqueue("UczenDziennik-multi.json", RegisterTest::class.java)
            repeat(4) { // 4x symbol
                enqueue("Logowanie-brak-dostepu.html", LoginTest::class.java)
            }

            start(3000)
        }

        val res = registerStudent.getUserSubjects().symbols
            .flatMap { it.schools }
            .flatMap { it.subjects }
            .filterIsInstance<RegisterStudent>()

        assertEquals(2, res.size)

        res[0].run {
            assertEquals(3881, studentId)
            assertEquals("Jan", studentName)
            assertEquals("Kowalski", studentSurname)
            assertEquals(121, classId)
            // assertEquals("Publiczna szkoła Wulkanowego nr 1 w fakelog.cf", schoolName)
            assertEquals("Te", className)
        }

        res[1].run {
            assertEquals(3881, studentId)
            assertEquals("Jan", studentName)
            assertEquals("Kowalski", studentSurname)
            assertEquals(119, classId)
            // assertEquals("Publiczna szkoła Wulkanowego nr 1 w fakelog.cf", schoolName)
            assertEquals("Ti", className)
        }
    }

    @Test
    fun getStudents_kindergartenDiaries() = runTest {
        with(server) {
            enqueue("LoginPage-standard.html", LoginTest::class.java)
            enqueue("Logowanie-uonet.html", LoginTest::class.java)
            enqueue("Logowanie-uonet.html", LoginTest::class.java)
            enqueue("Login-success.html", LoginTest::class.java)

            enqueue("LoginPage-standard.html", LoginTest::class.java)
            enqueue("WitrynaUcznia.html", RegisterTest::class.java)
            enqueue("UczenCache.json", RegisterTest::class.java)
            enqueue("UczenDziennik-no-semester.json", RegisterTest::class.java)

            repeat(4) { // 4x symbol
                enqueue("Logowanie-brak-dostepu.html", LoginTest::class.java)
            }

            start(3000)
        }

        val res = registerStudent.getUserSubjects().symbols
            .flatMap { it.schools }
            .flatMap { it.subjects }
            .filterIsInstance<RegisterStudent>()

        assertEquals(1, res.size)

        res[0].run {
            assertEquals(1, studentId)
            assertEquals("Jan", studentName)
            assertEquals("Kowalski", studentSurname)
            assertEquals(0, classId) // always 0 for kindergarten
            // assertEquals("Publiczna szkoła Wulkanowego nr 1 w fakelog.cf", schoolName)
            // assertEquals("123456", schoolSymbol)
            // assertEquals(654321, userLoginId)
            // assertEquals("Jan Kowalski", fullname) // todo
            assertEquals(2016, semesters[0].schoolYear)
            assertEquals(2017, semesters[1].schoolYear)
        }
    }

    @Test
    fun getStudents_filterNoDiares() = runTest {
        with(server) {
            enqueue("LoginPage-standard.html", LoginTest::class.java)
            enqueue("Logowanie-uonet.html", LoginTest::class.java)
            enqueue("Login-success.html", LoginTest::class.java)

            enqueue("LoginPage-standard.html", LoginTest::class.java)
            enqueue("WitrynaUcznia.html", RegisterTest::class.java)
            enqueue("UczenCache.json", RegisterTest::class.java)
            enqueue("UczenDziennik-no-diary.json", RegisterTest::class.java)

            repeat(4) { // 4x symbol
                enqueue("Logowanie-brak-dostepu.html", LoginTest::class.java)
            }

            start(3000)
        }

        val res = registerStudent.getUserSubjects().symbols
            .flatMap { it.schools }
            .flatMap { it.subjects }

        assertEquals(0, res.size)
    }

    @Test
    fun getStudents_classNameOrder() = runTest {
        with(server) {
            enqueue("LoginPage-standard.html", LoginTest::class.java)
            enqueue("Logowanie-uonet.html", LoginTest::class.java)
            enqueue("Logowanie-uonet.html", LoginTest::class.java)
            enqueue("Login-success.html", LoginTest::class.java)
            enqueue("LoginPage-standard.html", LoginTest::class.java)
            enqueue("WitrynaUcznia.html", RegisterTest::class.java)
            enqueue("UczenCache.json", RegisterTest::class.java)
            enqueue("UczenDziennik.json", RegisterTest::class.java)
            repeat(4) { // 4x symbol
                enqueue("Logowanie-brak-dostepu.html", LoginTest::class.java)
            }

            start(3000)
        }

        val res = registerStudent.getUserSubjects().symbols
            .flatMap { it.schools }
            .flatMap { it.subjects }
            .filterIsInstance<RegisterStudent>()

        assertEquals(2, res.size)

        res[0].run {
            assertEquals(1, studentId)
            assertEquals("Jan", studentName)
            assertEquals("Kowalski", studentSurname)
            assertEquals(1, classId)
            assertEquals("A", className)
            // assertEquals("Publiczna szkoła Wulkanowego nr 1 w fakelog.cf", schoolName)
        }

        res[1].run {
            assertEquals(2, studentId)
            assertEquals("Joanna", studentName)
            assertEquals("Czerwińska", studentSurname)
            assertEquals(2, classId)
            assertEquals("A", className)
            // assertEquals("Publiczna szkoła Wulkanowego nr 1 w fakelog.cf", schoolName) // todo
        }
    }
}

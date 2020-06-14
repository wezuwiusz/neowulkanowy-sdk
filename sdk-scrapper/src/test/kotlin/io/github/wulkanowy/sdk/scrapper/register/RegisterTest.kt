package io.github.wulkanowy.sdk.scrapper.register

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import io.github.wulkanowy.sdk.scrapper.Scrapper
import io.github.wulkanowy.sdk.scrapper.grades.GradesTest
import io.github.wulkanowy.sdk.scrapper.login.LoginHelper
import io.github.wulkanowy.sdk.scrapper.login.LoginTest
import io.github.wulkanowy.sdk.scrapper.repository.RegisterRepository
import io.github.wulkanowy.sdk.scrapper.repository.StudentAndParentStartRepository
import io.github.wulkanowy.sdk.scrapper.service.LoginService
import io.github.wulkanowy.sdk.scrapper.service.RegisterService
import io.github.wulkanowy.sdk.scrapper.service.ServiceManager
import io.github.wulkanowy.sdk.scrapper.service.StudentAndParentService
import io.github.wulkanowy.sdk.scrapper.service.StudentService
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Test
import java.net.CookieManager

class RegisterTest : BaseLocalTest() {

    private val login by lazy {
        LoginHelper(Scrapper.LoginType.STANDARD, "http", "fakelog.localhost:3000", "default", CookieManager(),
            getService(LoginService::class.java, "http://fakelog.localhost:3000/", true, true, false, Scrapper.LoginType.STANDARD))
    }

    private val registerSnp by lazy {
        RegisterRepository("default", "janusz69", "jan123", false, login,
            getService(RegisterService::class.java, "http://fakelog.localhost:3000/Default/", true, false, false),
            getService(StudentAndParentService::class.java, "http://fakelog.localhost:3000/"),
            getService(StudentService::class.java, "http://fakelog.localhost:3000"),
            ServiceManager.UrlGenerator("http", "fakelog.localhost:3000", "default", "123")
        )
    }

    private val registerStudent by lazy {
        RegisterRepository("default", "jan@fakelog.localhost", "jan123", true, login,
            getService(RegisterService::class.java, "http://fakelog.localhost:3000/Default/", true, false, false),
            getService(StudentAndParentService::class.java, "http://fakelog.localhost:3000/"),
            getService(StudentService::class.java, "http://fakelog.localhost:3000", false),
            ServiceManager.UrlGenerator("http", "fakelog.localhost:3000", "default", "123")
        )
    }

    private val snp by lazy {
        StudentAndParentStartRepository("default", "0012345", 123,
            getService(StudentAndParentService::class.java, "http://fakelog.localhost:3000/"))
    }

    @Test
    fun pupilsTest_snp() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("LoginPage-standard.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Login-success.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("LoginPage-standard.html").readText()))
        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("WitrynaUczniaIRodzica.html").readText()))
        server.enqueue(MockResponse().setBody(GradesTest::class.java.getResource("OcenyWszystkie-details.html").readText()))
        // 4x symbol
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.start(3000)

        val res = runBlocking { registerSnp.getStudents() }

        assertEquals(1, res.size)
        assertEquals("Jan Kowal", res[0].studentName)
    }

    @Test
    fun filterStudentsByClass() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("LoginPage-standard.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Login-success.html").readText()))
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
            assertEquals("Jan Kowalski", studentName)
            assertEquals(121, classId)
            assertEquals("Publiczna szkoła Wulkanowego nr 1 w fakelog.cf", schoolName)
            assertEquals("2Te", className)
        }

        res[1].run {
            assertEquals(3881, studentId)
            assertEquals("Jan Kowalski", studentName)
            assertEquals(119, classId)
            assertEquals("Publiczna szkoła Wulkanowego nr 1 w fakelog.cf", schoolName)
            assertEquals("2Ti", className)
        }
    }

    @Test
    fun getStudents_filterDiariesWithoutSemester() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("LoginPage-standard.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Login-success.html").readText()))
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
            assertEquals("Jan Kowalski", studentName)
            assertEquals(1, classId)
            assertEquals("Publiczna szkoła Wulkanowego nr 1 w fakelog.cf", schoolName)
            assertEquals("1A", className)
        }
    }

    @Test
    fun getStudents_filterDiariesWithEmptySemester() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("LoginPage-standard.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Login-success.html").readText()))
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
            assertEquals("Jan Kowalski", studentName)
            assertEquals(1, classId)
            assertEquals("Publiczna szkoła Wulkanowego nr 1 w fakelog.cf", schoolName)
            assertEquals("1A", className)
        }
    }

    @Test
    fun getStudents_classNameOrder() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("LoginPage-standard.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Login-success.html").readText()))
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
            assertEquals("Jan Kowalski", studentName)
            assertEquals(1, classId)
            assertEquals("3A", className)
            assertEquals("Publiczna szkoła Wulkanowego nr 1 w fakelog.cf", schoolName)
        }

        res[1].run {
            assertEquals(2, studentId)
            assertEquals("Joanna Czerwińska", studentName)
            assertEquals(2, classId)
            assertEquals("3A", className)
            assertEquals("Publiczna szkoła Wulkanowego nr 1 w fakelog.cf", schoolName)
        }
    }

    @Test
    fun semestersTest() {
        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("WitrynaUczniaIRodzica.html").readText()))
        // 3x diary
        server.enqueue(MockResponse().setBody(GradesTest::class.java.getResource("OcenyWszystkie-details.html").readText()))
        server.enqueue(MockResponse().setBody(GradesTest::class.java.getResource("OcenyWszystkie-details.html").readText().replace("1234568", "1234570")))
        server.enqueue(MockResponse().setBody(GradesTest::class.java.getResource("OcenyWszystkie-details.html").readText().replace("1234568", "1234572")))
        server.start(3000)

        val res = runBlocking { snp.getSemesters() }

        assertEquals(6, res.size)
        assertEquals(1234567, res[0].semesterId)
        assertEquals(1100, res[0].diaryId)
        assertEquals(1234568, res[1].semesterId)
        assertEquals("1Ti 2015", res[1].diaryName)
        assertEquals(1234567, res[2].semesterId)
        assertEquals(1, res[2].semesterNumber)
    }

    @Test
    fun loginType_standard() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("LoginPage-standard.html").readText())) //
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Login-success.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("LoginPage-standard.html").readText())) //
        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("WitrynaUczniaIRodzica.html").readText()))
        server.enqueue(MockResponse().setBody(GradesTest::class.java.getResource("OcenyWszystkie-details.html").readText()))
        // 4x symbol
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.start(3000)

        val res = runBlocking { registerSnp.getStudents() }
        assertEquals(Scrapper.LoginType.STANDARD, res[0].loginType)
    }

    @Test
    fun loginType_adfs() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("ADFS-form-2.html").readText())) //
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("ADFS-form-2.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-cufs.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Login-success.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("ADFS-form-2.html").readText())) //
        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("WitrynaUczniaIRodzica.html").readText()))
        server.enqueue(MockResponse().setBody(GradesTest::class.java.getResource("OcenyWszystkie-details.html").readText()))
        // 4x symbol
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.start(3000)

        val res = runBlocking { registerSnp.getStudents() }
        assertEquals(Scrapper.LoginType.ADFS, res[0].loginType)
    }

    @Test
    fun loginType_adfsCards() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("ADFS-form-1.html").readText())) //
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("ADFS-form-1.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("ADFS-form-2.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-cufs.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Login-success.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("ADFS-form-1.html").readText())) //
        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("WitrynaUczniaIRodzica.html").readText()))
        server.enqueue(MockResponse().setBody(GradesTest::class.java.getResource("OcenyWszystkie-details.html").readText()))
        // 4x symbol
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.start(3000)

        val res = runBlocking { registerSnp.getStudents() }
        assertEquals(Scrapper.LoginType.ADFSCards, res[0].loginType)
    }

    @Test
    fun loginType_adfsLight() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("ADFSLight-form-1.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-cufs.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Login-success.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("ADFSLight-form-1.html").readText())) //
        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("WitrynaUczniaIRodzica.html").readText()))
        server.enqueue(MockResponse().setBody(GradesTest::class.java.getResource("OcenyWszystkie-details.html").readText()))
        // 4x symbol
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.start(3000)

        val res = runBlocking { registerSnp.getStudents() }
        assertEquals(Scrapper.LoginType.ADFSLight, res[0].loginType)
    }

    @Test
    fun loginType_adfsLight_resman() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("ADFSLight-form-resman.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-cufs.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Login-success.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("ADFSLight-form-1.html").readText())) //
        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("WitrynaUczniaIRodzica.html").readText()))
        server.enqueue(MockResponse().setBody(GradesTest::class.java.getResource("OcenyWszystkie-details.html").readText()))
        // 4x symbol
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.start(3000)

        val res = runBlocking { registerSnp.getStudents() }
        assertEquals(Scrapper.LoginType.ADFSLight, res[0].loginType)
    }

    @Test
    fun loginType_adfsLightScoped() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("ADFSLightScoped-form-1.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-cufs.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Login-success.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("ADFSLightScoped-form-1.html").readText().replace("Default", "default"))) //
        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("WitrynaUczniaIRodzica.html").readText()))
        server.enqueue(MockResponse().setBody(GradesTest::class.java.getResource("OcenyWszystkie-details.html").readText()))
        // 4x symbol
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-brak-dostepu.html").readText()))
        server.start(3000)

        val res = runBlocking { registerSnp.getStudents() }
        assertEquals(Scrapper.LoginType.ADFSLightScoped, res[0].loginType)
    }
}

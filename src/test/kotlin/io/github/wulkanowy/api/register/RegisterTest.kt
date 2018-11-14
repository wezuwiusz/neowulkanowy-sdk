package io.github.wulkanowy.api.register

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.api.BaseLocalTest
import io.github.wulkanowy.api.grades.GradesTest
import io.github.wulkanowy.api.login.LoginTest
import io.github.wulkanowy.api.repository.LoginRepository
import io.github.wulkanowy.api.repository.RegisterRepository
import io.github.wulkanowy.api.repository.StudentAndParentStartRepository
import io.github.wulkanowy.api.service.LoginService
import io.github.wulkanowy.api.service.RegisterService
import io.github.wulkanowy.api.service.StudentAndParentService
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Test

class RegisterTest : BaseLocalTest() {

    private val login by lazy {
        LoginRepository(Api.LoginType.STANDARD, "http", "fakelog.localhost:3000", "default",
                getService(LoginService::class.java, "http://fakelog.localhost:3000/"))
    }

    private val register by lazy {
        RegisterRepository("default", "jan@fakelog.localhost", "jan123", login,
                getService(RegisterService::class.java, "http://fakelog.localhost:3000/Default/", true, false),
                getService(StudentAndParentService::class.java, "http://fakelog.localhost:3000/"))
    }

    private val snp by lazy {
        StudentAndParentStartRepository("default", "0012345", 123,
                getService(StudentAndParentService::class.java, "http://fakelog.localhost:3000/"))
    }

    @Test
    fun pupilsTest() {
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

        val res = register.getPupils().blockingGet()

        assertEquals(1, res.size)
        assertEquals("Jan Kowal", res[0].studentName)
    }

    @Test
    fun semestersTest() {
        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("WitrynaUczniaIRodzica.html").readText()))
        // 3x diary
        server.enqueue(MockResponse().setBody(GradesTest::class.java.getResource("OcenyWszystkie-details.html").readText()))
        server.enqueue(MockResponse().setBody(GradesTest::class.java.getResource("OcenyWszystkie-details.html").readText().replace("1234568", "1234570")))
        server.enqueue(MockResponse().setBody(GradesTest::class.java.getResource("OcenyWszystkie-details.html").readText().replace("1234568", "1234572")))
        server.start(3000)

        val res = snp.getSemesters().blockingGet()

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

        val res = register.getPupils().blockingGet()
        assertEquals(Api.LoginType.STANDARD, res[0].loginType)
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

        val res = register.getPupils().blockingGet()
        assertEquals(Api.LoginType.ADFS, res[0].loginType)
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

        val res = register.getPupils().blockingGet()
        assertEquals(Api.LoginType.ADFSCards, res[0].loginType)
    }

    @Test
    fun loginType_adfsLight() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("ADFSLight-form-1.html").readText())) //
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("ADFSLight-form-1.html").readText()))
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

        val res = register.getPupils().blockingGet()
        assertEquals(Api.LoginType.ADFSLight, res[0].loginType)
    }
}

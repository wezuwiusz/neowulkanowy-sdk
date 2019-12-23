package io.github.wulkanowy.sdk.scrapper.repository

import io.github.wulkanowy.sdk.scrapper.Scrapper
import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import io.github.wulkanowy.sdk.scrapper.grades.GradesTest
import io.github.wulkanowy.sdk.scrapper.login.LoginTest
import io.github.wulkanowy.sdk.scrapper.register.RegisterTest
import io.github.wulkanowy.sdk.scrapper.register.Semester
import io.reactivex.observers.TestObserver
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Test

class StudentAndParentStartRepositoryTest : BaseLocalTest() {

    private val api by lazy {
        Scrapper().apply {
            ssl = false
            host = "fakelog.localhost:3000" //
            symbol = "Default"
            email = "jan@fakelog.cf"
            password = "jan123"
            schoolSymbol = "123456"
            studentId = 1
            diaryId = 101
            useNewStudent = false
        }
    }

    @Test
    fun getSemesters_invalidStartPage() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Login-success.html").readText()))
        server.start(3000) //

        api.loginType = Scrapper.LoginType.STANDARD

        val semesters = api.getSemesters()
        val semestersObserver = TestObserver<List<Semester>>()
        semesters.subscribe(semestersObserver)
        semestersObserver.assertTerminated()
        semestersObserver.assertErrorMessage("Unknow page with title: Uonet+")
    }

    @Test
    fun getSemesters_invalidGradesPage() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-standard.html").readText()))

        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Login-success.html").readText()))

        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("WitrynaUczniaIRodzica.html").readText()))
        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("WitrynaUczniaIRodzica.html").readText()))
        server.start(3000) //

        api.loginType = Scrapper.LoginType.STANDARD

        val semesters = api.getSemesters()
        val semestersObserver = TestObserver<List<Semester>>()
        semesters.subscribe(semestersObserver)
        semestersObserver.assertTerminated()
        semestersObserver.assertErrorMessage("Unknow page with title: Witryna ucznia i rodzica – Strona główna")
    }

    @Test
    fun getSemesters() {
        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("WitrynaUczniaIRodzica.html").readText()))
        server.enqueue(MockResponse().setBody(GradesTest::class.java.getResource("OcenyWszystkie-details.html").readText()))
        server.enqueue(MockResponse().setBody(GradesTest::class.java.getResource("OcenyWszystkie-details.html").readText()))
        server.enqueue(MockResponse().setBody(GradesTest::class.java.getResource("OcenyWszystkie-details.html").readText()))
        server.start(3000) //

        api.loginType = Scrapper.LoginType.STANDARD

        val semesters = api.getSemesters()
        val semestersObserver = TestObserver<List<Semester>>()
        semesters.subscribe(semestersObserver)
        semestersObserver.assertComplete()

        val items = semestersObserver.values()[0]

        assertEquals(6, items.size)

        assertEquals(1234567, items[0].semesterId)
        assertEquals(1234568, items[1].semesterId)
    }

    @Test
    fun getSemesters_normal() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-standard.html").readText()))

        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Login-success.html").readText()))

        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("WitrynaUczniaIRodzica.html").readText()))
        server.enqueue(MockResponse().setBody(GradesTest::class.java.getResource("OcenyWszystkie-details.html").readText()))
        server.enqueue(MockResponse().setBody(GradesTest::class.java.getResource("OcenyWszystkie-details.html").readText()))
        server.enqueue(MockResponse().setBody(GradesTest::class.java.getResource("OcenyWszystkie-details.html").readText()))
        server.start(3000) //

        api.loginType = Scrapper.LoginType.STANDARD

        val semesters = api.getSemesters()
        val semestersObserver = TestObserver<List<Semester>>()
        semesters.subscribe(semestersObserver)
        semestersObserver.assertComplete()

        val items = semestersObserver.values()[0]

        assertEquals(6, items.size)

        assertEquals(1234567, items[0].semesterId)
        assertEquals(1234568, items[1].semesterId)
    }

    @Test
    fun getSemesters_ADFS() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("ADFS-form-2.html").readText())) //

        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("ADFS-form-2.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-cufs.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Login-success.html").readText()))

        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("WitrynaUczniaIRodzica.html").readText()))
        server.enqueue(MockResponse().setBody(GradesTest::class.java.getResource("OcenyWszystkie-details.html").readText()))
        server.enqueue(MockResponse().setBody(GradesTest::class.java.getResource("OcenyWszystkie-details.html").readText()))
        server.enqueue(MockResponse().setBody(GradesTest::class.java.getResource("OcenyWszystkie-details.html").readText()))
        server.start(3000) //

        api.loginType = Scrapper.LoginType.ADFS

        val semesters = api.getSemesters()
        val semestersObserver = TestObserver<List<Semester>>()
        semesters.subscribe(semestersObserver)
        semestersObserver.assertComplete()

        val items = semestersObserver.values()[0]

        assertEquals(6, items.size)

        assertEquals(1234567, items[0].semesterId)
        assertEquals(1234568, items[1].semesterId)
    }

    @Test
    fun getSemesters_ADFSLight() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("ADFSLight-form-1.html").readText()))

        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-cufs.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Login-success.html").readText()))

        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("WitrynaUczniaIRodzica.html").readText()))
        server.enqueue(MockResponse().setBody(GradesTest::class.java.getResource("OcenyWszystkie-details.html").readText()))
        server.enqueue(MockResponse().setBody(GradesTest::class.java.getResource("OcenyWszystkie-details.html").readText()))
        server.enqueue(MockResponse().setBody(GradesTest::class.java.getResource("OcenyWszystkie-details.html").readText()))
        server.start(3000) //

        api.loginType = Scrapper.LoginType.ADFSLight

        val semesters = api.getSemesters()
        val semestersObserver = TestObserver<List<Semester>>()
        semesters.subscribe(semestersObserver)
        semestersObserver.assertComplete()

        val items = semestersObserver.values()[0]

        assertEquals(6, items.size)

        assertEquals(1234567, items[0].semesterId)
        assertEquals(1234568, items[1].semesterId)
    }

    @Test
    fun getSemesters_ADFSCards() {
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("ADFS-form-1.html").readText()))

        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("ADFS-form-1.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("ADFS-form-2.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-cufs.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Logowanie-uonet.html").readText()))
        server.enqueue(MockResponse().setBody(LoginTest::class.java.getResource("Login-success.html").readText()))

        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("WitrynaUczniaIRodzica.html").readText()))
        server.enqueue(MockResponse().setBody(GradesTest::class.java.getResource("OcenyWszystkie-details.html").readText()))
        server.enqueue(MockResponse().setBody(GradesTest::class.java.getResource("OcenyWszystkie-details.html").readText()))
        server.enqueue(MockResponse().setBody(GradesTest::class.java.getResource("OcenyWszystkie-details.html").readText()))
        server.start(3000) //

        api.loginType = Scrapper.LoginType.ADFSCards

        val semesters = api.getSemesters()
        val semestersObserver = TestObserver<List<Semester>>()
        semesters.subscribe(semestersObserver)
        semestersObserver.assertComplete()

        val items = semestersObserver.values()[0]

        assertEquals(6, items.size)

        assertEquals(1234567, items[0].semesterId)
        assertEquals(1234568, items[1].semesterId)
        assertEquals(2015, items[0].schoolYear)
        assertEquals(2015, items[1].schoolYear)
    }
}

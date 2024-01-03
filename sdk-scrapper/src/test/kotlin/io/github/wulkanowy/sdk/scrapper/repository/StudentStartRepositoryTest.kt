package io.github.wulkanowy.sdk.scrapper.repository

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import io.github.wulkanowy.sdk.scrapper.Scrapper
import io.github.wulkanowy.sdk.scrapper.login.LoginTest
import io.github.wulkanowy.sdk.scrapper.messages.MessagesTest
import io.github.wulkanowy.sdk.scrapper.register.RegisterTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class StudentStartRepositoryTest : BaseLocalTest() {

    private val api by lazy {
        Scrapper().apply {
            ssl = false
            loginType = Scrapper.LoginType.STANDARD
            host = "fakelog.localhost:3000" //
            symbol = "Default"
            email = "jan@fakelog.cf"
            password = "jan123"
            schoolId = "123456"
            diaryId = 101
        }
    }

    @Test
    fun getSemesters() {
        with(server) {
            enqueue("UczenDziennik.json", RegisterTest::class.java)
            start(3000) //
        }

        api.studentId = 1
        api.classId = 1

        val semesters = runBlocking { api.getSemesters() }

        assertEquals(6, semesters.size)

        assertEquals(1234568, semesters[0].semesterId)
        assertEquals(1234567, semesters[1].semesterId)
        assertEquals(2018, semesters[0].schoolYear)
        assertEquals(2018, semesters[1].schoolYear)

        assertEquals(1234566, semesters[2].semesterId)
        assertEquals(2017, semesters[2].schoolYear)
        assertEquals(2017, semesters[3].schoolYear)
    }

    @Test
    fun getSemesters_empty() {
        with(server) {
            enqueue("UczenDziennik.json", RegisterTest::class.java)
            start(3000) //
        }

        api.studentId = 1
        api.classId = 2 //

        val semesters = runBlocking { api.getSemesters() }

        assertEquals(0, semesters.size)
    }

    @Test
    fun getSemesters_studentWithMultiClasses() {
        with(server) {
            enqueue("UczenDziennik-multi.json", RegisterTest::class.java)
            start(3000)
        }

        api.studentId = 3881
        api.classId = 121

        val semesters = runBlocking { api.getSemesters() }

        assertEquals(2, semesters.size)

        assertEquals(714, semesters[0].semesterId)
        assertEquals(713, semesters[1].semesterId)
    }

    @Test
    fun getSemesters_graduate() {
        with(server) {
            enqueue("UczenDziennik.json", RegisterTest::class.java)
            start(3000) //
        }

        api.studentId = 2
        api.classId = 2

        val semesters = runBlocking { api.getSemesters() }

        assertEquals(6, semesters.size)

        assertEquals(1234568, semesters[0].semesterId)
        assertEquals(1234567, semesters[1].semesterId)
    }

    @Test
    fun getSemesters_normal() {
        with(server) {
            enqueue("Logowanie-standard.html", LoginTest::class.java)

            enqueue("Logowanie-uonet.html", LoginTest::class.java)
            enqueue("Login-success.html", LoginTest::class.java)

            // enqueue("Start.html", MessagesTest::class.java)
            enqueue("WitrynaUcznia.html", RegisterTest::class.java)

            enqueue("UczenDziennik.json", RegisterTest::class.java)
            start(3000) //
        }

        with(api) {
            studentId = 1
            classId = 1
            loginType = Scrapper.LoginType.STANDARD
        }

        val semesters = runBlocking { api.getSemesters() }

        assertEquals(6, semesters.size)

        assertEquals(1234568, semesters[0].semesterId)
        assertEquals(1234567, semesters[1].semesterId)
    }

    @Test
    fun getSemesters_ADFS() {
        with(server) {
            enqueue("ADFS.html", LoginTest::class.java) //

            enqueue("Logowanie-cufs.html", LoginTest::class.java)
            enqueue("Logowanie-uonet.html", LoginTest::class.java)
            enqueue("Login-success.html", LoginTest::class.java)

            // enqueue("Start.html", MessagesTest::class.java)
            enqueue("WitrynaUcznia.html", RegisterTest::class.java)

            enqueue("UczenDziennik.json", RegisterTest::class.java)
            start(3000) //
        }

        with(api) {
            studentId = 1
            classId = 1
            loginType = Scrapper.LoginType.ADFS
        }

        val semesters = runBlocking { api.getSemesters() }

        assertEquals(6, semesters.size)

        assertEquals(1234568, semesters[0].semesterId)
        assertEquals(1234567, semesters[1].semesterId)
    }

    @Test
    fun getSemesters_ADFSLight() {
        with(server) {
            enqueue("ADFSLight-form-1.html", LoginTest::class.java)

            enqueue("Logowanie-cufs.html", LoginTest::class.java)
            enqueue("Logowanie-uonet.html", LoginTest::class.java)
            enqueue("Login-success.html", LoginTest::class.java)

            // enqueue("Start.html", MessagesTest::class.java)
            enqueue("WitrynaUcznia.html", RegisterTest::class.java)

            enqueue("UczenDziennik.json", RegisterTest::class.java)
            start(3000) //
        }

        with(api) {
            studentId = 1
            classId = 1
            loginType = Scrapper.LoginType.ADFSLight
        }

        val semesters = runBlocking { api.getSemesters() }

        assertEquals(6, semesters.size)

        assertEquals(1234568, semesters[0].semesterId)
        assertEquals(1234567, semesters[1].semesterId)
    }

    @Test
    fun getSemesters_ADFSCards() {
        with(server) {
            enqueue("unknown-error.txt", RegisterTest::class.java)

            enqueue("ADFSCards.html", LoginTest::class.java)
            enqueue("Logowanie-cufs.html", LoginTest::class.java)
            enqueue("Logowanie-uonet.html", LoginTest::class.java)
            enqueue("Login-success.html", LoginTest::class.java)

            // enqueue("Start.html", MessagesTest::class.java)
            enqueue("WitrynaUcznia.html", RegisterTest::class.java)

            enqueue("UczenDziennik.json", RegisterTest::class.java)
            start(3000) //
        }

        with(api) {
            studentId = 1
            classId = 1
            loginType = Scrapper.LoginType.ADFSCards
        }

        val semesters = runBlocking { api.getSemesters() }

        assertEquals(6, semesters.size)

        assertEquals(1234568, semesters[0].semesterId)
        assertEquals(1234567, semesters[1].semesterId)
        assertEquals(2018, semesters[0].schoolYear)
        assertEquals(2018, semesters[1].schoolYear)
    }
}

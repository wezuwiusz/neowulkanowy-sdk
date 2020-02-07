package io.github.wulkanowy.sdk.scrapper.repository

import io.github.wulkanowy.sdk.scrapper.Scrapper
import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import io.github.wulkanowy.sdk.scrapper.register.RegisterTest
import io.github.wulkanowy.sdk.scrapper.register.Semester
import io.reactivex.observers.TestObserver
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
            schoolSymbol = "123456"
            diaryId = 101
            useNewStudent = true
        }
    }

    @Test
    fun getSemesters() {
        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("UczenDziennik.json").readText()))
        server.start(3000) //

        api.studentId = 1
        api.classId = 1

        val semesters = api.getSemesters()
        val semestersObserver = TestObserver<List<Semester>>()
        semesters.subscribe(semestersObserver)
        semestersObserver.assertComplete()

        val items = semestersObserver.values()[0]

        assertEquals(6, items.size)

        assertEquals(1234568, items[0].semesterId)
        assertEquals(1234567, items[1].semesterId)
        assertEquals(2018, items[0].schoolYear)
        assertEquals(2018, items[1].schoolYear)

        assertEquals(1234566, items[2].semesterId)
        assertEquals(2017, items[2].schoolYear)
        assertEquals(2017, items[3].schoolYear)
        assertTrue(items.single { it.current }.current)
    }

    @Test
    fun getSemesters_empty() {
        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("UczenDziennik.json").readText()))
        server.start(3000) //

        api.studentId = 1
        api.classId = 2 //

        val semesters = api.getSemesters()
        val semestersObserver = TestObserver<List<Semester>>()
        semesters.subscribe(semestersObserver)
        semestersObserver.assertComplete()

        val items = semestersObserver.values()[0]

        assertEquals(0, items.size)
    }

    @Test
    fun getSemesters_studentWithMultiClasses() {
        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("UczenDziennik-multi.json").readText()))
        server.start(3000) //

        api.studentId = 3881
        api.classId = 121

        val semesters = api.getSemesters()
        val semestersObserver = TestObserver<List<Semester>>()
        semesters.subscribe(semestersObserver)
        semestersObserver.assertComplete()

        val items = semestersObserver.values()[0]

        assertEquals(2, items.size)

        assertEquals(714, items[0].semesterId)
        assertEquals(713, items[1].semesterId)
        assertTrue(items.single { it.current }.current)
    }

    @Test
    fun getSemesters_graduate() {
        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("UczenDziennik.json").readText()))
        server.start(3000) //

        api.studentId = 2
        api.classId = 2

        val semesters = api.getSemesters()
        val semestersObserver = TestObserver<List<Semester>>()
        semesters.subscribe(semestersObserver)
        semestersObserver.assertComplete()

        val items = semestersObserver.values()[0]

        assertEquals(6, items.size)

        assertEquals(1234568, items[0].semesterId)
        assertEquals(1234568, items.single { it.current }.semesterId)
        assertEquals(1234567, items[1].semesterId)
        assertTrue(items.single { it.current }.current)
        assertTrue(items[0].current)
    }
}

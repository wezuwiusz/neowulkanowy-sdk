package io.github.wulkanowy.sdk.scrapper.homepage

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import io.github.wulkanowy.sdk.scrapper.home.LuckyNumber
import io.github.wulkanowy.sdk.scrapper.repository.HomepageRepository
import io.github.wulkanowy.sdk.scrapper.service.HomepageService
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HomepageTest : BaseLocalTest() {

    private val repo by lazy {
        HomepageRepository(getService(HomepageService::class.java, "http://fakelog.localhost:3000/", false))
    }

    @Test
    fun getSelfGovernments() {
        server.enqueue(MockResponse().setBody(HomepageTest::class.java.getResource("Index.html").readText()))
        server.enqueue("GetSelfGovernments.json")
        server.start(3000)

        val units = repo.getSelfGovernments().blockingGet()
        assertEquals(1, units.size)
        assertEquals("ZST-I", units[0].unitName)

        val members = units[0].people
        assertEquals(3, members.size)
        with(members[0]) {
            assertEquals("Jan Michał Kowalski", name)
            assertEquals("Przewodniczący", position)
            assertEquals("3tm (T 17)", division)
            assertEquals(0, id)
        }
    }

    @Test
    fun getStudentsTrips() {
    }

    @Test
    fun getLastGrades() {
        server.enqueue(MockResponse().setBody(HomepageTest::class.java.getResource("Index.html").readText()))
        server.enqueue(MockResponse().setBody(HomepageTest::class.java.getResource("GetLastNotes.json").readText()))
        server.start(3000)

        val res = repo.getLastGrades().blockingGet()
        assertEquals(
            listOf(
                "j. angielski: 1, 6",
                "j. polski: 6, 1",
                "matematyka: 4+, -"
            ), res
        )
    }

    @Test
    fun getFreeDays() {
        server.enqueue(MockResponse().setBody(HomepageTest::class.java.getResource("Index.html").readText()))
        server.enqueue(MockResponse().setBody(HomepageTest::class.java.getResource("GetFreeDays.json").readText()))
        server.start(3000)

        val res = repo.getFreeDays().blockingGet()
        assertEquals(
            listOf(
                "Czwartek (20.06.2019) - Sobota (31.08.2019) - Ferie letnie",
                "Czwartek (15.08.2019) - Wniebowzięcie Najświętszej Maryi Panny"
            ), res
        )
    }

    @Test
    fun getKidsLuckyNumbers() {
    }

    @Test
    fun getKidsLessonPlan() {
    }

    @Test
    fun getLastHomework() {
    }

    @Test
    fun getLastTests() {
    }

    @Test
    fun getLastStudentLessons() {
    }

    @Test
    fun getLuckyNumber_single() {
        server.enqueue(MockResponse().setBody(HomepageTest::class.java.getResource("Index.html").readText()))
        server.enqueue(MockResponse().setBody(HomepageTest::class.java.getResource("GetKidsLuckyNumbers.json").readText()))
        server.start(3000)

        val number = repo.getKidsLuckyNumbers().blockingGet()
        assertEquals(listOf(LuckyNumber("", "SPL", 18)), number)

        server.takeRequest()
        with(server.takeRequest().body.readUtf8()) {
            assertTrue(startsWith("permissions=YRQQQJH"))
            assertTrue(endsWith("XzKdrhz9Ke9dkHzx"))
        }
    }

    @Test
    fun getLuckyNumber_multi() {
        server.enqueue(MockResponse().setBody(HomepageTest::class.java.getResource("Index.html").readText()))
        server.enqueue(MockResponse().setBody(HomepageTest::class.java.getResource("GetKidsLuckyNumbers-multi-institution.json").readText()))
        server.start(3000)

        val numbers = repo.getKidsLuckyNumbers().blockingGet()
        assertEquals(listOf(
            LuckyNumber("002547", "T", 37),
            LuckyNumber("010472", "ZSP Warcie", 12)
        ), numbers)

        server.takeRequest()
        with(server.takeRequest().body.readUtf8()) {
            assertTrue(startsWith("permissions=YRQQQJH"))
            assertTrue(endsWith("XzKdrhz9Ke9dkHzx"))
        }
    }
}

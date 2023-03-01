package io.github.wulkanowy.sdk.scrapper.homepage

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import io.github.wulkanowy.sdk.scrapper.home.LuckyNumber
import io.github.wulkanowy.sdk.scrapper.repository.HomepageRepository
import io.github.wulkanowy.sdk.scrapper.service.HomepageService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate.of

class HomepageTest : BaseLocalTest() {

    private val repo by lazy {
        HomepageRepository(getService(HomepageService::class.java, "http://fakelog.localhost:3000/", false))
    }

    @Test
    fun getDirectorInformation() {
        with(server) {
            enqueue("Index.html")
            enqueue("GetDirectorInformation.json")
            start(3000)
        }

        val infos = runBlocking { repo.getDirectorInformation() }
        assertEquals(2, infos.size)

        with(infos[0]) {
            assertEquals(of(2020, 10, 12), date)
            assertEquals("Zmaina obuwia", subject)
            assertEquals("Informuję nauczycieli i uczni&#243;w, że...", content)
        }

        with(infos[1]) {
            assertEquals(of(2020, 11, 2), date)
            assertEquals("Dzień wolny od zajęć dydaktycznych", subject)
            assertEquals("Dzień wolny od zajęć dydaktycznych<br />02.11.2020 – poniedziałek", content)
        }
    }

    @Test
    fun getSelfGovernments() {
        with(server) {
            enqueue("Index.html")
            enqueue("GetSelfGovernments.json")
            start(3000)
        }

        val units = runBlocking { repo.getSelfGovernments() }
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
    fun getStudentThreats() {
        with(server) {
            enqueue("Index.html")
            enqueue("GetStudentThreats.json")
            start(3000)
        }

        val threats = runBlocking { repo.getStudentThreats() }
        assertEquals(1, threats.size)
        assertEquals("Jan Kowalski matematyka", threats[0])
    }

    @Test
    fun getLastGrades() {
        with(server) {
            enqueue("Index.html")
            enqueue("GetLastNotes.json")
            start(3000)
        }

        val res = runBlocking { repo.getLastGrades() }
        val expected = listOf(
            "j. angielski: 1, 6",
            "j. polski: 6, 1",
            "matematyka: 4+, -",
        )
        assertEquals(expected, res)
    }

    @Test
    fun getFreeDays() {
        with(server) {
            enqueue("Index.html")
            enqueue("GetFreeDays.json")
            start(3000)
        }

        val res = runBlocking { repo.getFreeDays() }
        val expected = listOf(
            "Czwartek (20.06.2019) - Sobota (31.08.2019) - Ferie letnie",
            "Czwartek (15.08.2019) - Wniebowzięcie Najświętszej Maryi Panny",
        )
        assertEquals(expected, res)
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
        with(server) {
            enqueue("Index.html")
            enqueue("GetKidsLuckyNumbers.json")
            start(3000)
        }

        val number = runBlocking { repo.getKidsLuckyNumbers() }
        assertEquals(listOf(LuckyNumber("", "SPL", 18)), number)

        server.takeRequest()
        with(server.takeRequest().body.readUtf8()) {
            assertTrue(startsWith("permissions=YRQQQJH"))
            assertTrue(endsWith("XzKdrhz9Ke9dkHzx"))
        }
    }

    @Test
    fun getLuckyNumber_multi() {
        with(server) {
            enqueue("Index.html")
            enqueue("GetKidsLuckyNumbers-multi-institution.json")
            start(3000)
        }

        val numbers = runBlocking { repo.getKidsLuckyNumbers() }
        val expected = listOf(
            LuckyNumber("002547", "T", 37),
            LuckyNumber("010472", "ZSP Warcie", 12),
        )
        assertEquals(expected, numbers)

        server.takeRequest()
        with(server.takeRequest().body.readUtf8()) {
            assertTrue(startsWith("permissions=YRQQQJH"))
            assertTrue(endsWith("XzKdrhz9Ke9dkHzx"))
        }
    }
}

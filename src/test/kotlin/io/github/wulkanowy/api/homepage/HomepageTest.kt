package io.github.wulkanowy.api.homepage

import io.github.wulkanowy.api.BaseLocalTest
import io.github.wulkanowy.api.repository.HomepageRepository
import io.github.wulkanowy.api.service.HomepageService
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Test

class HomepageTest : BaseLocalTest() {

    private val repo by lazy {
        HomepageRepository(getService(HomepageService::class.java, "http://fakelog.localhost:3000/", false))
    }

    @Test
    fun getSelfGovernments() {
    }

    @Test
    fun getStudentsTrips() {
    }

    @Test
    fun getLastGrades() {
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
}

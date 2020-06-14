package io.github.wulkanowy.sdk.scrapper

import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceSummaryTest
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.Test

class ScrapperTest : BaseLocalTest() {

    @Test
    fun changeTest() {
        server.enqueue(MockResponse().setBody(AttendanceSummaryTest::class.java.getResource("Przedmioty.json").readText()))
        server.start(3000) //

        val api = Scrapper().apply {
            loginType = Scrapper.LoginType.STANDARD
            ssl = false
            host = "fakelog.localhost" //
            symbol = "Default"
            email = "jan@fakelog.cf"
            password = "jan123"
            schoolSymbol = "123456"
            studentId = 1
            diaryId = 101
        }

        try {
            runBlocking { api.getSubjects() }
        } catch (e: Throwable) {
            assert(true) //
        }

        api.apply {
            host = "fakelog.localhost:3000" //
        }

        try {
            runBlocking { api.getSubjects() }
        } catch (e: Throwable) {
            assert(false) //
        }
    }
}

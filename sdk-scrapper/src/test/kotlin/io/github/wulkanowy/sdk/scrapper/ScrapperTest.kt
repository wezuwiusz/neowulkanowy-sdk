package io.github.wulkanowy.sdk.scrapper

import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceSummaryTest
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ScrapperTest : BaseLocalTest() {

    @Test
    fun changeTest() {
        with(server) {
            enqueue("Przedmioty.json", AttendanceSummaryTest::class.java)
            start(3000) //
        }

        val api = Scrapper().apply {
            loginType = Scrapper.LoginType.STANDARD
            ssl = false
            host = "fakelog.localhost" //
            symbol = "Default"
            email = "jan@fakelog.cf"
            password = "jan123"
            schoolId = "123456"
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
            e.printStackTrace()
            assert(false) //
        }
    }
}

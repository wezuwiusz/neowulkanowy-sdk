package io.github.wulkanowy.sdk.scrapper

import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceSummaryTest
import io.github.wulkanowy.sdk.scrapper.attendance.Subject
import io.reactivex.observers.TestObserver
import okhttp3.mockwebserver.MockResponse
import org.junit.Test

class ScrapperTest : BaseLocalTest() {

    @Test
    fun changeTest() {
        server.enqueue(MockResponse().setBody(AttendanceSummaryTest::class.java.getResource("Frekwencja.html").readText()))
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

        val subjects = api.getSubjects()
        val subjectsObserver = TestObserver<List<Subject>>()
        subjects.subscribe(subjectsObserver)
        subjectsObserver.assertNotComplete() //

        api.apply {
            host = "fakelog.localhost:3000" //
        }

        val subjects2 = api.getSubjects()
        val subjectsObserver2 = TestObserver<List<Subject>>()
        subjects2.subscribe(subjectsObserver2)
        subjectsObserver2.assertComplete() //
    }
}

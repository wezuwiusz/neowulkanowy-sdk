package io.github.wulkanowy.sdk.hebe

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

@Ignore
@OptIn(ExperimentalCoroutinesApi::class)
class HebeRemoteTest {

    private val hebe = Hebe()

    @Before
    fun setUp() {
        with(hebe) {
            logLevel = HttpLoggingInterceptor.Level.BODY
            keyId = "///"
            privatePem = "///"
            baseUrl = "https://api.fakelog.cf/powiatwulkanowy/"
            pupilId = 1234
            schoolId = "008520"
            deviceModel = "Pixel 4a (5G)"
        }
    }

    @Test
    fun `register device`() = runTest {
        val res = hebe.register(
            firebaseToken = "",
            token = "3S1JDDR",
            pin = "212343",
            symbol = "gminanowosolna",
        )
        assertTrue(res.privatePem.isNotEmpty())
    }

    @Test
    fun `get students`() = runTest {
        val res = hebe.getStudents(hebe.baseUrl)
        assertTrue(res.isNotEmpty())
    }

    @Test
    fun `get grades`() = runTest {
        val grades = hebe.getGrades(559)
        assertTrue(grades.isNotEmpty())
    }

    @Test
    fun `get grades summary`() = runTest {
        val summaries = hebe.getGradesSummary(559)
        assertTrue(summaries.isNotEmpty())
    }

    @Test
    fun `get exams`() = runTest {
        val exams = hebe.getExams()
        assertTrue(exams.isNotEmpty())
    }
}

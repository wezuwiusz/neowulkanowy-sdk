package io.github.wulkanowy.sdk.scrapper.attendance

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import io.github.wulkanowy.sdk.scrapper.toFormat
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.HttpURLConnection
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
class AttendancePlusTest : BaseLocalTest() {

    @Test
    fun `get simple attendance list without exemptions`() = runTest {
        val repo = getStudentPlusRepo {
            it.enqueue("FrekwencjaPlus.json")
            it.enqueue("UsprawiedliwieniaPlus-empty.json")
        }

        val res = repo.getAttendance(LocalDate.now(), null, 1, 2, 3)
        assertEquals(2, res.size)

        with(res[0]) {
            assertEquals(7, number)
            assertEquals(17, timeId)
            assertEquals(LocalDateTime.of(2024, 2, 25, 0, 0), date)
            assertEquals("Matematyka", subject)
            assertEquals(AttendanceCategory.ABSENCE_UNEXCUSED, category)
            assertFalse(excusable)
            assertNull(excuseStatus)
        }

        with(res[1]) {
            assertEquals(8, number)
            assertEquals(18, timeId)
            assertEquals(LocalDateTime.of(2024, 2, 25, 0, 0), date)
            assertEquals("Technika", subject)
            assertEquals(AttendanceCategory.ABSENCE_UNEXCUSED, category)
            assertFalse(excusable)
            assertNull(excuseStatus)
        }
    }

    @Test
    fun `get simple attendance list with one lesson exemption`() = runTest {
        val repo = getStudentPlusRepo {
            it.enqueue("FrekwencjaPlus.json")
            it.enqueue("UsprawiedliwieniaPlus-lesson.json")
        }

        val res = repo.getAttendance(LocalDate.now(), null, 1, 2, 3)
        assertEquals(2, res.size)

        with(res[0]) {
            assertEquals(AttendanceCategory.ABSENCE_UNEXCUSED, category)
            assertTrue(excusable)
            assertNull(excuseStatus)
        }

        with(res[1]) {
            assertEquals(AttendanceCategory.ABSENCE_UNEXCUSED, category)
            assertFalse(excusable)
            assertEquals(SentExcuseStatus.ACCEPTED, excuseStatus)
        }
    }

    @Test
    fun `get simple attendance list with day exemption`() = runTest {
        val repo = getStudentPlusRepo {
            it.enqueue("FrekwencjaPlus.json")
            it.enqueue("UsprawiedliwieniaPlus-day.json")
        }

        val res = repo.getAttendance(LocalDate.now(), null, 1, 2, 3)
        assertEquals(2, res.size)

        with(res[0]) {
            assertEquals(AttendanceCategory.ABSENCE_UNEXCUSED, category)
            assertFalse(excusable)
            assertEquals(SentExcuseStatus.WAITING, excuseStatus)
        }

        with(res[1]) {
            assertEquals(AttendanceCategory.ABSENCE_UNEXCUSED, category)
            assertFalse(excusable)
            assertEquals(SentExcuseStatus.WAITING, excuseStatus)
        }
    }

    @Test
    fun `excuse for absence`() = runTest {
        val repo = getStudentPlusRepo {
            it.enqueue(MockResponse().setResponseCode(HttpURLConnection.HTTP_NO_CONTENT))
        }

        val requestDate = LocalDateTime.of(2024, 2, 25, 1, 1, 0)

        val result = repo.excuseForAbsence(
            absents = listOf(
                Absent(date = requestDate, timeId = 1),
            ),
            content = null,
            studentId = 12,
            diaryId = 23,
            unitId = 34,
        )
        assertTrue(result)

        val request = server.takeRequest()
        val payload = Json.decodeFromString<AttendanceExcusePlusRequest>(request.body.readUtf8())
        assertEquals("", payload.content)
        assertEquals(Base64.encode("12-23-1-34".toByteArray()), payload.key)
        assertEquals(1, payload.excuses.size)
        assertEquals(requestDate.toFormat("yyyy-MM-dd'T'HH:mm:ss"), payload.excuses.single().date)
        assertEquals(1, payload.excuses.single().lessonHourId)
    }
}

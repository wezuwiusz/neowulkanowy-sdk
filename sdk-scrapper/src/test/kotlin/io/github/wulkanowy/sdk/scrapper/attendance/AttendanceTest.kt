package io.github.wulkanowy.sdk.scrapper.attendance

import io.github.wulkanowy.sdk.scrapper.Scrapper
import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import io.github.wulkanowy.sdk.scrapper.register.RegisterTest
import io.github.wulkanowy.sdk.scrapper.repository.StudentRepository
import io.github.wulkanowy.sdk.scrapper.service.StudentService
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.threeten.bp.LocalDateTime

class AttendanceTest : BaseLocalTest() {

    private val student by lazy {
        runBlocking { getStudentRepo(AttendanceTest::class.java, "Frekwencja.json").getAttendance(getLocalDate(2018, 10, 1), null) }
    }

    override fun getStudentRepo(testClass: Class<*>, fixture: String, loginType: Scrapper.LoginType, autoLogin: Boolean): StudentRepository {
        server.enqueue(MockResponse().setBody(testClass.getResource(fixture).readText()))
        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("WitrynaUcznia.html").readText()))
        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("UczenCache.json").readText()))
        return StudentRepository(getService(StudentService::class.java, server.url("/").toString(), false, true, true, loginType))
    }

    @Test
    fun getAttendance() {
        assertEquals(8, student.size)
    }

    @Test
    fun getAttendance_presence() {
        student[0].run {
            // wtorek, 1
            assertEquals(1, number)
            assertEquals(76, timeId)
            assertEquals(getDate(2018, 10, 2), date)
            assertEquals("Zajęcia artystyczne", subject)
            assertEquals("Obecność", name)
            assertTrue(presence)
            assertFalse(excused)
            assertEquals(SentExcuse.Status.WAITING, excuseStatus)

            assertFalse(absence)
            assertFalse(exemption)
            assertFalse(lateness)
            assertFalse(deleted)
            assertFalse(excusable)
        }
    }

    @Test
    fun getAttendance_absence() {
        student[1].run {
            // wtorek, 2
            assertEquals(2, number)
            assertEquals(77, timeId)
            assertEquals(getDate(2018, 10, 2), date)
            assertEquals("Informatyka", subject)
            assertEquals("Nieobecność nieusprawiedliwiona", name)
            assertTrue(absence)
            assertFalse(excused)
            assertEquals(SentExcuse.Status.ACCEPTED, excuseStatus)

            assertFalse(exemption)
            assertFalse(presence)
            assertFalse(lateness)
            assertFalse(deleted)
            assertFalse(excusable)
        }
    }

    @Test
    fun getAttendance_absenceExcused() {
        student[2].run {
            // środa, 1
            assertEquals(1, number)
            assertEquals(76, timeId)
            assertEquals(getDate(2018, 10, 3), date)
            assertEquals("Matematyka", subject)
            assertEquals("Nieobecność usprawiedliwiona", name)
            assertTrue(absence)
            assertTrue(excused)
            assertEquals(SentExcuse.Status.DENIED, excuseStatus)

            assertFalse(exemption)
            assertFalse(presence)
            assertFalse(lateness)
            assertFalse(deleted)
            assertFalse(excusable)
        }
    }

    @Test
    fun getAttendance_lateness() {
        student[3].run {
            // środa, 2
            assertEquals(2, number)
            assertEquals(77, timeId)
            assertEquals(getDate(2018, 10, 3), date)
            assertEquals("Godzina wychowawcza", subject)
            assertEquals("Spóźnienie nieusprawiedliwione", name)
            assertTrue(lateness)
            assertFalse(excused)
            assertNull(excuseStatus)

            assertFalse(exemption)
            assertFalse(presence)
            assertFalse(absence)
            assertFalse(deleted)
            assertTrue(excusable)
        }
    }

    @Test
    fun getAttendance_latenessExcused() {
        student[4].run {
            // czwartek, 1
            assertEquals(1, number)
            assertEquals(76, timeId)
            assertEquals(getDate(2018, 10, 4), date)
            assertEquals("Historia", subject)
            assertEquals("Spóźnienie usprawiedliwione", name)
            assertTrue(lateness)
            assertTrue(excused)
            assertNull(excuseStatus)

            assertFalse(exemption)
            assertFalse(presence)
            assertFalse(absence)
            assertFalse(deleted)
            assertFalse(excusable)
        }
    }

    @Test
    fun getAttendance_absenceForSchoolReason() {
        student[5].run {
            // czwartek, 2
            assertEquals(2, number)
            assertEquals(77, timeId)
            assertEquals(getDate(2018, 10, 4), date)
            assertEquals("Język angielski", subject)
            assertEquals("Nieobecność z przyczyn szkolnych", name)
            assertTrue(presence)
            assertFalse(excused)
            assertNull(excuseStatus)

            assertFalse(lateness)
            assertFalse(exemption)
            assertFalse(absence)
            assertFalse(deleted)
            assertFalse(excusable)
        }
    }

    @Test
    fun getAttendance_exemption() {
        student[6].run {
            // piątek, 1
            assertEquals(1, number)
            assertEquals(76, timeId)
            assertEquals(getDate(2018, 10, 5), date)
            assertEquals("Informatyka", subject)
            assertEquals("Zwolnienie", name)
            assertTrue(exemption)
            assertFalse(excused)
            assertNull(excuseStatus)

            assertFalse(lateness)
            assertFalse(presence)
            assertFalse(absence)
            assertFalse(deleted)
            assertFalse(excusable)
        }
    }

    @Test
    fun getAttendance_unknown() {
        student[7].run {
            // piątek, 2
            assertEquals(2, number)
            assertEquals(77, timeId)
            assertEquals(getDate(2018, 10, 5), date)
            assertEquals("Informatyka", subject)
            assertEquals("Nieznany", name)
            assertFalse(exemption)
            assertFalse(excused)
            assertNull(excuseStatus)

            assertFalse(lateness)
            assertFalse(presence)
            assertFalse(absence)
            assertFalse(deleted)
            assertFalse(excusable)
        }
    }

    @Test
    fun excuseForAbsence() {
        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("WitrynaUcznia.html").readText()))

        val absents = listOf(
            Absent(
                date = LocalDateTime.of(2019, 2, 11, 15, 53, 9),
                timeId = 1
            ),
            Absent(
                date = LocalDateTime.of(2019, 2, 11, 15, 53, 9),
                timeId = 2
            ),
            Absent(
                date = LocalDateTime.of(2019, 2, 11, 15, 53, 9),
                timeId = 3
            ),
            Absent(
                date = LocalDateTime.of(2019, 2, 12, 15, 53, 9),
                timeId = null
            )
        )

        runBlocking {
            getStudentRepo(AttendanceTest::class.java, "Usprawiedliwione.json").excuseForAbsence(
                absents = absents,
                content = "Test"
            )
        }

        server.takeRequest()

        val request = server.takeRequest()
        val expected = jsonParser.parse(AttendanceTest::class.java.getResource("Usprawiedliwienie.json").readText())

        assertEquals(expected, jsonParser.parse(request.body.readUtf8()))
        assertEquals(
            "7SaCmj247xiKA4nQcTqLJ8J56UnZpxL3zLNENZjKAdFQN3xN26EwRdhAezyo5Wx3P2iWVPLTc3fpjPCNMbEPLmxF4RrLeaAGdQevu8pgbEB2TocqfBPjWzNLyHXBcqxKM",
            request.getHeader("X-V-RequestVerificationToken")
        )
        assertEquals("2w68d2SFGnvRtVhuXoLYdxL3ue4F9yqD", request.getHeader("X-V-AppGuid"))
        assertEquals("18.07.0003.31856", request.getHeader("X-V-AppVersion"))
    }
}

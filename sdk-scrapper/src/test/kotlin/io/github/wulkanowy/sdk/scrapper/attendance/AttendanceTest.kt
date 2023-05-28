package io.github.wulkanowy.sdk.scrapper.attendance

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import io.github.wulkanowy.sdk.scrapper.register.RegisterTest
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class AttendanceTest : BaseLocalTest() {

    private val student by lazy {
        val repo = getStudentRepo {
            it.enqueue("WitrynaUcznia.html", RegisterTest::class.java)
            it.enqueue("UczenCache.json", RegisterTest::class.java)
            it.enqueue("Frekwencja.json", AttendanceTest::class.java)
        }
        runBlocking { repo.getAttendance(getLocalDate(2018, 10, 1), null, 1, 1) }
    }

    @Test
    fun getAttendance() {
        assertEquals(9, student.size)
    }

    @Test
    fun getAttendance_presence() {
        student[0].run {
            // wtorek, 1
            assertEquals(1, number)
            assertEquals(76, timeId)
            assertEquals(getDate(2018, 10, 2), date)
            assertEquals("Zajęcia artystyczne", subject)
            assertEquals(AttendanceCategory.PRESENCE, category)
            assertEquals(SentExcuseStatus.WAITING, excuseStatus)
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
            assertEquals(AttendanceCategory.ABSENCE_UNEXCUSED, category)
            assertEquals(SentExcuseStatus.ACCEPTED, excuseStatus)
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
            assertEquals(AttendanceCategory.ABSENCE_EXCUSED, category)
            assertEquals(SentExcuseStatus.DENIED, excuseStatus)
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
            assertEquals(AttendanceCategory.UNEXCUSED_LATENESS, category)
            assertNull(excuseStatus)

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
            assertEquals(AttendanceCategory.EXCUSED_LATENESS, category)
            assertNull(excuseStatus)
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
            assertEquals(AttendanceCategory.ABSENCE_FOR_SCHOOL_REASONS, category)
            assertNull(excuseStatus)
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
            assertEquals(AttendanceCategory.EXEMPTION, category)
            assertNull(excuseStatus)
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
            assertEquals(AttendanceCategory.UNKNOWN, category)
            assertNull(excuseStatus)
            assertFalse(excusable)
        }
    }

    @Test
    fun getAttendance_unknownSubject() {
        student[8].run {
            // piątek, 3
            assertEquals(3, number)
            assertEquals(78, timeId)
            assertEquals(getDate(2018, 10, 5), date)
            assertEquals(null, subject)
            assertEquals(AttendanceCategory.UNKNOWN, category)
            assertNull(excuseStatus)
            assertFalse(excusable)
        }
    }

    @Test
    fun getAttendance_requestDateFormat() {
        val repo = getStudentRepo {
            it.enqueue("WitrynaUcznia.html", RegisterTest::class.java)
            it.enqueue("UczenCache.json", RegisterTest::class.java)
            it.enqueue("Frekwencja.json", AttendanceTest::class.java)
        }
        runBlocking { repo.getAttendance(getLocalDate(2018, 10, 1), null, 1, 2) }

        server.takeRequest()
        server.takeRequest()
        val request = server.takeRequest()

        val requestObject = Json.decodeFromString<AttendanceRequest>(request.body.readUtf8())
        assertEquals(getDate(2018, 10, 1, 0, 0, 0), requestObject.date)
        assertEquals(-1, requestObject.typeId)
    }

    @Test
    fun excuseForAbsence() {
        server.enqueue("WitrynaUcznia.html", RegisterTest::class.java)

        val absents = listOf(
            Absent(
                date = LocalDateTime.of(2019, 2, 11, 15, 53, 9),
                timeId = 1,
            ),
            Absent(
                date = LocalDateTime.of(2019, 2, 11, 15, 53, 9),
                timeId = 2,
            ),
            Absent(
                date = LocalDateTime.of(2019, 2, 11, 15, 53, 9),
                timeId = 3,
            ),
            Absent(
                date = LocalDateTime.of(2019, 2, 12, 15, 53, 9),
                timeId = null,
            ),
        )

        runBlocking {
            getStudentRepo(AttendanceTest::class.java, "Usprawiedliwione.json").excuseForAbsence(
                absents = absents,
                content = "Test",
            )
        }

        server.takeRequest()

        val request = server.takeRequest()

        val expected = Json.decodeFromString<AttendanceExcuseRequest>(AttendanceTest::class.java.getResource("Usprawiedliwienie.json")!!.readText())
        val actual = Json.decodeFromString<AttendanceExcuseRequest>(request.body.readUtf8())

        assertEquals(expected, actual)
        assertEquals(
            "7SaCmj247xiKA4nQcTqLJ8J56UnZpxL3zLNENZjKAdFQN3xN26EwRdhAezyo5Wx3P2iWVPLTc3fpjPCNMbEPLmxF4RrLeaAGdQevu8pgbEB2TocqfBPjWzNLyHXBcqxKM",
            request.getHeader("X-V-RequestVerificationToken"),
        )
        assertEquals("2w68d2SFGnvRtVhuXoLYdxL3ue4F9yqD", request.getHeader("X-V-AppGuid"))
        assertEquals("18.07.0003.31856", request.getHeader("X-V-AppVersion"))
    }
}

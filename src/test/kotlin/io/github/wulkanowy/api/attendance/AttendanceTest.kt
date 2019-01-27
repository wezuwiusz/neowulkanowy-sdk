package io.github.wulkanowy.api.attendance

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.api.BaseLocalTest
import io.github.wulkanowy.api.register.RegisterTest
import io.github.wulkanowy.api.repository.StudentRepository
import io.github.wulkanowy.api.service.StudentService
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AttendanceTest : BaseLocalTest() {

    private val snp by lazy {
        getSnpRepo(AttendanceTest::class.java, "Frekwencja.html").getAttendance(getLocalDate(2018, 10, 1)).blockingGet()
    }

    private val student by lazy {
        getStudentRepo(AttendanceTest::class.java, "Frekwencja.json").getAttendance(getLocalDate(2018, 10, 1)).blockingGet()
    }

    override fun getStudentRepo(testClass: Class<*>, fixture: String, loginType: Api.LoginType): StudentRepository {
        server.enqueue(MockResponse().setBody(testClass.getResource(fixture).readText()))
        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("WitrynaUcznia.html").readText()))
        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("UczenCache.json").readText()))
        return StudentRepository(getService(StudentService::class.java, server.url("/").toString(), false, true, true, loginType))
    }

    @Test
    fun getAttendance() {
        assertEquals(7, snp.size)
        assertEquals(8, student.size)
    }

    @Test
    fun getAttendance_presence() {
        listOf(snp[0], student[0]).map {
            it.run {
                // wtorek, 1
                assertEquals(1, number)
                assertEquals(getDate(2018, 10, 2), date)
                assertEquals("Zajęcia artystyczne", subject)
                assertEquals("Obecność", name)
                assertTrue(presence)

                assertFalse(absence)
                assertFalse(exemption)
                assertFalse(lateness)
                assertFalse(excused)
                assertFalse(deleted)
            }
        }
    }

    @Test
    fun getAttendance_absence() {
        listOf(snp[1], student[1]).map {
            it.run {
                // wtorek, 2
                assertEquals(2, number)
                assertEquals(getDate(2018, 10, 2), date)
                assertEquals("Informatyka", subject)
                assertEquals("Nieobecność nieusprawiedliwiona", name)
                assertTrue(absence)
                assertFalse(excused)

                assertFalse(exemption)
                assertFalse(presence)
                assertFalse(lateness)
                assertFalse(deleted)
            }
        }
    }

    @Test
    fun getAttendance_absenceExcused() {
        listOf(snp[2], student[2]).map {
            it.run {
                // środa, 1
                assertEquals(1, number)
                assertEquals(getDate(2018, 10, 3), date)
                assertEquals("Matematyka", subject)
                assertEquals("Nieobecność usprawiedliwiona", name)
                assertTrue(absence)
                assertTrue(excused)

                assertFalse(exemption)
                assertFalse(presence)
                assertFalse(lateness)
                assertFalse(deleted)
            }
        }
    }

    @Test
    fun getAttendance_lateness() {
        listOf(snp[3], student[3]).map {
            it.run {
                // środa, 2
                assertEquals(2, number)
                assertEquals(getDate(2018, 10, 3), date)
                assertEquals("Godzina wychowawcza", subject)
                assertEquals("Spóźnienie nieusprawiedliwione", name)
                assertTrue(lateness)
                assertFalse(excused)

                assertFalse(exemption)
                assertFalse(presence)
                assertFalse(absence)
                assertFalse(deleted)
            }
        }
    }

    @Test
    fun getAttendance_latenessExcused() {
        listOf(snp[4], student[4]).map {
            it.run {
                // czwartek, 1
                assertEquals(1, number)
                assertEquals(getDate(2018, 10, 4), date)
                assertEquals("Historia", subject)
                assertEquals("Spóźnienie usprawiedliwione", name)
                assertTrue(lateness)
                assertTrue(excused)

                assertFalse(exemption)
                assertFalse(presence)
                assertFalse(absence)
                assertFalse(deleted)
            }
        }
    }

    @Test
    fun getAttendance_absenceForSchoolReason() {
        listOf(snp[5], student[5]).map {
            it.run {
                // czwartek, 2
                assertEquals(2, number)
                assertEquals(getDate(2018, 10, 4), date)
                assertEquals("Język angielski", subject)
                assertEquals("Nieobecność z przyczyn szkolnych", name)
                assertTrue(presence)

                assertFalse(excused)
                assertFalse(lateness)
                assertFalse(exemption)
                assertFalse(absence)
                assertFalse(deleted)
            }
        }
    }

    @Test
    fun getAttendance_exemption() {
        listOf(snp[6], student[6]).map {
            it.run {
                // piątek, 1
                assertEquals(1, number)
                assertEquals(getDate(2018, 10, 5), date)
                assertEquals("Informatyka", subject)
                assertEquals("Zwolnienie", name)
                assertTrue(exemption)

                assertFalse(lateness)
                assertFalse(excused)
                assertFalse(presence)
                assertFalse(absence)
                assertFalse(deleted)
            }
        }
    }

    @Test
    fun getAttendance_unknown() {
        listOf(student[7]).map {
            it.run {
                // piątek, 2
                assertEquals(2, number)
                assertEquals(getDate(2018, 10, 5), date)
                assertEquals("Informatyka", subject)
                assertEquals("Nieznany", name)
                assertFalse(exemption)

                assertFalse(lateness)
                assertFalse(excused)
                assertFalse(presence)
                assertFalse(absence)
                assertFalse(deleted)
            }
        }
    }
}

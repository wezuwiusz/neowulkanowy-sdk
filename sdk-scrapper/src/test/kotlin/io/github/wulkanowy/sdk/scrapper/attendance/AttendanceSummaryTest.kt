package io.github.wulkanowy.sdk.scrapper.attendance

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Month.DECEMBER
import java.time.Month.JUNE
import java.time.Month.SEPTEMBER

class AttendanceSummaryTest : BaseLocalTest() {

    private val attendance by lazy {
        runBlocking { getStudentRepo(AttendanceSummaryTest::class.java, "StatystykiFrekwencji.json").getAttendanceSummary(-1) }
    }

    private val subjects by lazy {
        runBlocking { getStudentRepo(AttendanceSummaryTest::class.java, "Przedmioty.json").getSubjects() }
    }

    @Test
    fun getAttendanceSummary() {
        assertEquals(10, attendance.size)
    }

    @Test
    fun getSubjects() {
        with(subjects[0]) {
            assertEquals("Wszystkie", name)
            assertEquals(-1, value)
        }
    }

    @Test
    fun getAttendance_september() {
        with(attendance[0]) {
            assertEquals(SEPTEMBER, month)
            assertEquals(32, presence)
            assertEquals(1, absence)
            assertEquals(2, absenceExcused)
            assertEquals(3, absenceForSchoolReasons)
            assertEquals(4, lateness)
            assertEquals(5, latenessExcused)
            assertEquals(6, exemption)
        }
    }

    @Test
    fun getAttendance_december() {
        with(attendance[3]) {
            assertEquals(DECEMBER, month)
            assertEquals(55, presence)
            assertEquals(0, absence)
            assertEquals(33, absenceExcused)
            assertEquals(0, absenceForSchoolReasons)
            assertEquals(5, lateness)
            assertEquals(0, latenessExcused)
            assertEquals(0, exemption)
        }
    }

    @Test
    fun getAttendance_august() {
        with(attendance[9]) {
            assertEquals(JUNE, month)
            assertEquals(124, presence)
            assertEquals(6, absence)
            assertEquals(2, absenceExcused)
            assertEquals(0, absenceForSchoolReasons)
            assertEquals(5, lateness)
            assertEquals(0, latenessExcused)
            assertEquals(0, exemption)
        }
    }
}

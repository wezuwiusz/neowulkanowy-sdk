package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.mobile.BaseLocalTest
import io.github.wulkanowy.sdk.scrapper.attendance.Attendance
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Date

class AttendanceMapperTest : BaseLocalTest() {

    @Test
    fun getAttendance_presence() {
        createAttendance(Attendance.Category.PRESENCE).mapAttendance()[0].run {
            assertTrue(presence)
            assertFalse(excused)

            assertFalse(absence)
            assertFalse(exemption)
            assertFalse(lateness)
        }
    }

    @Test
    fun getAttendance_absence() {
        createAttendance(Attendance.Category.ABSENCE_UNEXCUSED).mapAttendance()[0].run {
            assertTrue(absence)
            assertFalse(excused)

            assertFalse(exemption)
            assertFalse(presence)
            assertFalse(lateness)
        }
    }

    @Test
    fun getAttendance_absenceExcused() {
        createAttendance(Attendance.Category.ABSENCE_EXCUSED).mapAttendance()[0].run {
            assertTrue(absence)
            assertTrue(excused)

            assertFalse(exemption)
            assertFalse(presence)
            assertFalse(lateness)
        }
    }

    @Test
    fun getAttendance_lateness() {
        createAttendance(Attendance.Category.UNEXCUSED_LATENESS).mapAttendance()[0].run {
            assertTrue(lateness)
            assertFalse(excused)

            assertFalse(exemption)
            assertFalse(presence)
            assertFalse(absence)
        }
    }

    @Test
    fun getAttendance_latenessExcused() {
        createAttendance(Attendance.Category.EXCUSED_LATENESS).mapAttendance()[0].run {
            assertTrue(lateness)
            assertTrue(excused)

            assertFalse(exemption)
            assertFalse(presence)
            assertFalse(absence)
        }
    }

    @Test
    fun getAttendance_absenceForSchoolReason() {
        createAttendance(Attendance.Category.ABSENCE_FOR_SCHOOL_REASONS).mapAttendance()[0].run {
            assertTrue(presence)
            assertFalse(excused)

            assertFalse(lateness)
            assertFalse(exemption)
            assertFalse(absence)
        }
    }

    @Test
    fun getAttendance_exemption() {
        createAttendance(Attendance.Category.EXEMPTION).mapAttendance()[0].run {
            assertTrue(exemption)
            assertFalse(excused)

            assertFalse(lateness)
            assertFalse(presence)
            assertFalse(absence)
        }
    }

    @Test
    fun getAttendance_unknown() {
        createAttendance(Attendance.Category.UNKNOWN).mapAttendance()[0].run {
            assertFalse(exemption)
            assertFalse(excused)

            assertFalse(lateness)
            assertFalse(presence)
            assertFalse(absence)
        }
    }

    private fun createAttendance(cat: Attendance.Category) = listOf(Attendance().apply {
        number = 0
        category = cat
        subject = ""
        date = Date()
        timeId = 1
        excusable = false
        excuseStatus = null
    })
}

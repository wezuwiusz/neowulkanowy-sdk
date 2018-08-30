package io.github.wulkanowy.api.attendance

import io.github.wulkanowy.api.BaseTest
import org.junit.Assert.*
import org.junit.Test

class AttendanceTest : BaseTest() {

    private val full by lazy {
        getSnpRepo(AttendanceTest::class.java, "Frekwencja-full.html").getAttendance("636648768000000000").blockingGet()
    }

    private val excellent by lazy {
        getSnpRepo(AttendanceTest::class.java, "Frekwencja-excellent.html").getAttendance("636648768000000000").blockingGet()
    }

    @Test
    fun getAttendanceFull() {
        assertTrue(full.isNotEmpty())
        assertEquals(38, full.size)
    }

    @Test fun getAttendanceExcellent() {
        assertTrue(excellent.isNotEmpty())
        assertEquals(22, excellent.size)
    }

    @Test fun getLessonSubject() {
        assertEquals("Uroczyste rozpoczęcie roku szkolnego 2015/2016", excellent[0].subject)
        assertEquals("Geografia", excellent[11].subject)

        assertEquals("Naprawa komputera", full[14].subject)
        assertEquals("Religia", full[23].subject)
        assertEquals("Metodologia programowania", full[34].subject)
    }

    @Test fun getLessonIsPresence() {
        assertEquals(Attendance.Types.PRESENCE, excellent[0].type)
        assertEquals(Attendance.Types.PRESENCE, excellent[15].type)

        assertEquals(Attendance.Types.PRESENCE, full[0].type)
        assertEquals(Attendance.Types.PRESENCE, full[21].type)
        assertNotEquals(Attendance.Types.PRESENCE, full[36].type)
        assertNotEquals(Attendance.Types.PRESENCE, full[37].type)
    }


    @Test fun getLessonIsAbsenceUnexcused() {
        assertNotEquals(Attendance.Types.ABSENCE_UNEXCUSED, excellent[0].type)

        assertEquals(Attendance.Types.ABSENCE_UNEXCUSED, full[14].type)
        assertNotEquals(Attendance.Types.ABSENCE_UNEXCUSED, full[24].type)
        assertNotEquals(Attendance.Types.ABSENCE_UNEXCUSED, full[37].type)
    }

    @Test fun getLessonIsAbsenceExcused() {
        assertNotEquals(Attendance.Types.ABSENCE_EXCUSED, excellent[0].type)

        assertNotEquals(Attendance.Types.ABSENCE_EXCUSED, full[5].type)
        assertNotEquals(Attendance.Types.ABSENCE_EXCUSED, full[10].type)
        assertEquals(Attendance.Types.ABSENCE_EXCUSED, full[36].type)
    }

    @Test fun getLessonIsAbsenceForSchoolReasons() {
        assertNotEquals(Attendance.Types.ABSENCE_FOR_SCHOOL_REASONS, excellent[6].type)

        assertEquals(Attendance.Types.ABSENCE_FOR_SCHOOL_REASONS, full[19].type)
        assertNotEquals(Attendance.Types.ABSENCE_FOR_SCHOOL_REASONS, full[0].type)
        assertNotEquals(Attendance.Types.ABSENCE_FOR_SCHOOL_REASONS, full[37])
    }

    @Test fun getLessonIsUnexcusedLateness() {
        assertNotEquals(Attendance.Types.UNEXCUSED_LATENESS, excellent[7].type)

        assertEquals(Attendance.Types.UNEXCUSED_LATENESS, full[12].type)
        assertNotEquals(Attendance.Types.UNEXCUSED_LATENESS, full[13].type)
        assertNotEquals(Attendance.Types.UNEXCUSED_LATENESS, full[36].type)
    }

    @Test fun getLessonIsExcusedLateness() {
        assertNotEquals(Attendance.Types.EXCUSED_LATENESS, excellent[8].type)

        assertEquals(Attendance.Types.EXCUSED_LATENESS, full[13].type)
        assertNotEquals(Attendance.Types.EXCUSED_LATENESS, full[14].type)
        assertNotEquals(Attendance.Types.EXCUSED_LATENESS, full[35].type)
    }

    @Test fun getLessonIsExemption() {
        assertNotEquals(Attendance.Types.EXEMPTION, excellent[9].type)

        assertNotEquals(Attendance.Types.EXEMPTION, full[0].type)
        assertNotEquals(Attendance.Types.EXEMPTION, full[15].type)
        assertEquals(Attendance.Types.EXEMPTION, full[37].type)
    }
}

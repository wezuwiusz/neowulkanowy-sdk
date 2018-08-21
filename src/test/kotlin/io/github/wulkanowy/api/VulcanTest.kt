package io.github.wulkanowy.api

import io.github.wulkanowy.api.attendance.Attendance
import io.github.wulkanowy.api.exams.Exam
import io.github.wulkanowy.api.grades.Grade
import io.github.wulkanowy.api.notes.Note
import io.reactivex.observers.TestObserver
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class VulcanTest {

    private var vulcan =  Vulcan()

    @Before fun setUp() {
        vulcan.apply {
            email = "admin"
            password = "admin"
            schoolId = "123456"
            studentId = "303"
            diaryId = "420"
        }
    }

    @Test fun attendanceTest() {
        val attendance = vulcan.getAttendance("636648768000000000")
        val attendanceObserver = TestObserver<List<Attendance>>()
        attendance.subscribe(attendanceObserver)
        attendanceObserver.assertComplete()

        val values = attendanceObserver.values()[0]

        assertEquals(0, values[0].number)
        assertEquals("Fizyka", values[0].subject)
        assertEquals(getDate(2018, 6, 18), values[0].date)

        assertEquals(Attendance.Types.PRESENCE, values[0].type)
        assertEquals(Attendance.Types.EXCUSED_LATENESS, values[1].type)
        assertEquals(Attendance.Types.ABSENCE_UNEXCUSED, values[3].type)
        assertEquals(Attendance.Types.EXEMPTION, values[4].type)
        assertEquals(Attendance.Types.ABSENCE_EXCUSED, values[6].type)
        assertEquals(Attendance.Types.ABSENCE_FOR_SCHOOL_REASONS, values[9].type)
        assertEquals(Attendance.Types.UNEXCUSED_LATENESS, values[12].type)

        assertEquals(1, values[1].number)
    }

    @Test fun examsTest() {
        val exams = vulcan.getExams("636630624000000000")
        val examsObserver = TestObserver<List<Exam>>()
        exams.subscribe(examsObserver)
        examsObserver.assertComplete()

        val values = examsObserver.values()[0]

        assertEquals(getDate(2018, 5, 9), values[0].date)
        assertEquals(getDate(2018, 4, 1), values[0].entryDate)
        assertEquals("Język angielski 1Ti|J1", values[0].subject)
        assertEquals("Sprawdzian", values[0].type)
        assertEquals("słownictwo(kultura)", values[0].description)
        assertEquals("Anyż Zofia [AZ]", values[0].teacher)
    }

    @Test fun notesTest() {
        val notes = vulcan.getNotes()
        val notesObserver = TestObserver<List<Note>>()
        notes.subscribe(notesObserver)
        notesObserver.assertComplete()

        val values = notesObserver.values()[0]

        assertEquals(getDate(2018, 3, 26), values[0].date)
        assertEquals("Janusz Tracz", values[0].teacher)
        assertEquals("Udział w konkursie szkolnym +20 pkt", values[0].category)
        assertEquals("+ 20p za udział w Konkursie Języka Angielskiego", values[0].content)
    }

    @Test fun gradesTest() {
        val grades = vulcan.getGrades(864)
        val gradesObserver = TestObserver<List<Grade>>()
        grades.subscribe(gradesObserver)
        gradesObserver.assertComplete()

        val values = gradesObserver.values()[0]

        assertEquals("Zajęcia z wychowawcą", values[0].subject)
        assertEquals("5", values[0].value)
        assertEquals("000000", values[0].color)
        assertEquals("A1", values[0].symbol)
        assertEquals("Dzień Kobiet w naszej klasie", values[0].description)
        assertEquals("1.00", values[0].weight)
        assertEquals(getDate(2017, 3, 21), values[0].date)
        assertEquals("Patryk Maciejewski", values[0].teacher)

        assertEquals("STR", values[4].symbol)
        assertEquals("", values[4].description)
    }

    private fun getDate(year: Int, month: Int, day: Int): Date {
        return Date.from(LocalDate.of(year, month, day)
                .atStartOfDay(ZoneId.systemDefault()).toInstant())
    }
}

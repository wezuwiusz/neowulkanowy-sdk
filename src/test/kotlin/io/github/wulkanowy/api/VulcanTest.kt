package io.github.wulkanowy.api

import io.github.wulkanowy.api.attendance.Attendance
import io.github.wulkanowy.api.exams.Exam
import io.github.wulkanowy.api.grades.Grade
import io.github.wulkanowy.api.interceptor.LoginInterceptor
import io.github.wulkanowy.api.notes.Note
import io.github.wulkanowy.api.repository.StudentAndParentRepository
import io.reactivex.observers.TestObserver
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.net.CookieManager
import java.net.CookiePolicy
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class VulcanTest {

    private lateinit var snp: StudentAndParentRepository

    @Before fun setUp() {
        val cookieManager = CookieManager()
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)

        val loginInterceptor = LoginInterceptor(
                "admin",
                "admin",
                "Default",
                "fakelog.cf",
                "303",
                "420",
                cookieManager
        )

        snp = StudentAndParentRepository(
                "https://uonetplus-opiekun.fakelog.cf",
                "Default",
                "123456",
                OkHttpClient().newBuilder()
                        .cookieJar(JavaNetCookieJar(cookieManager))
                        .addInterceptor(loginInterceptor)
                        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                        .build()
        )
    }

    @Test fun attendanceTest() {
        val attendance = snp.getAttendance("636697786721570000")
        val attendanceObserver = TestObserver<List<Attendance>>()
        attendance.subscribe(attendanceObserver)
        attendanceObserver.assertComplete()

        val values = attendanceObserver.values()[0]

        assertEquals(0, values[0].number)
        assertEquals("Fizyka", values[0].subject)
        assertEquals(getDate(2018, 8, 13), values[0].date)

        assertEquals(Attendance.Types.PRESENCE, values[0].type)
        assertEquals(Attendance.Types.ABSENCE_UNEXCUSED, values[1].type)
        assertEquals(Attendance.Types.ABSENCE_EXCUSED, values[2].type)
        assertEquals(Attendance.Types.ABSENCE_FOR_SCHOOL_REASONS, values[3].type)
        assertEquals(Attendance.Types.UNEXCUSED_LATENESS, values[4].type)

        assertEquals(Attendance.Types.EXCUSED_LATENESS, values[5].type)
        assertEquals(Attendance.Types.EXEMPTION, values[6].type)

        assertEquals(1, values[5].number)
    }

    @Test fun examsTest() {
        val exams = snp.getExams("636703910653480000")
        val examsObserver = TestObserver<List<Exam>>()
        exams.subscribe(examsObserver)

        val values = examsObserver.values()[0]

        assertEquals(getDate(2018, 5, 9), values[0].date)
        assertEquals(getDate(2018, 4, 1), values[0].entryDate)
        assertEquals("Język angielski 1Ti|J1", values[0].subject)
        assertEquals("Sprawdzian", values[0].type)
        assertEquals("słownictwo(kultura)", values[0].description)
        assertEquals("Anyż Zofia [AZ]", values[0].teacher)
    }

    @Test fun notesTest() {
        val notes = snp.getNotes()
        val notesObserver = TestObserver<List<Note>>()
        notes.subscribe(notesObserver)
        notesObserver.assertComplete()

        val values = notesObserver.values()[0]

        assertEquals("Janusz Tracz", values[0].teacher)
        assertEquals("Udział w konkursie szkolnym +20 pkt", values[0].category)
        assertEquals("+ 20p za udział w Konkursie Języka Angielskiego", values[0].content)
    }

    @Test fun gradesTest() {
        val grades = snp.getGrades(123)
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

package io.github.wulkanowy.api

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

    @Test fun notesTest() {
        val notes = snp.getNotes()
        val notesSubscriber = TestObserver<List<Note>>()
        notes.subscribe(notesSubscriber)
        notesSubscriber.assertComplete()

        val values = notesSubscriber.values()[0]

        assertEquals("Janusz Tracz", values[0].teacher)
        assertEquals("Udział w konkursie szkolnym +20 pkt", values[0].category)
        assertEquals("+ 20p za udział w Konkursie Języka Angielskiego", values[0].content)
    }

    @Test fun gradesTest() {
        val grades = snp.getGrades(123)
        val gradesSubscriber = TestObserver<List<Grade>>()
        grades.subscribe(gradesSubscriber)
        gradesSubscriber.assertComplete()

        val values = gradesSubscriber.values()[0]

        assertEquals("Zajęcia z wychowawcą", values[0].subject)
        assertEquals("5", values[0].value)
        assertEquals("000000", values[0].color)
        assertEquals("A1", values[0].symbol)
        assertEquals("Dzień Kobiet w naszej klasie", values[0].description)
        assertEquals("1.00", values[0].weight)
        assertEquals(Date.from(LocalDate.of(2017, 3, 21)
                .atStartOfDay(ZoneId.systemDefault()).toInstant()), values[0].date)
        assertEquals("Patryk Maciejewski", values[0].teacher)

        assertEquals("STR", values[4].symbol)
        assertEquals("", values[4].description)
    }

}

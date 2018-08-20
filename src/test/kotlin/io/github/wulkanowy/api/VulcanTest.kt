package io.github.wulkanowy.api

import io.github.wulkanowy.api.interceptor.LoginInterceptor
import io.github.wulkanowy.api.notes.Note
import io.github.wulkanowy.api.notes.NotesResponse
import io.github.wulkanowy.api.repository.StudentAndParentRepository
import io.reactivex.observers.TestObserver
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.assertEquals
import org.junit.Test
import java.net.CookieManager
import java.net.CookiePolicy

class VulcanTest {

    @Test fun snpTest() {
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

        val snp = StudentAndParentRepository(
                "https://uonetplus-opiekun.fakelog.cf",
                "Default",
                "123456",
                OkHttpClient().newBuilder()
                        .cookieJar(JavaNetCookieJar(cookieManager))
                        .addInterceptor(loginInterceptor)
                        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                        .build()
        )

        val notes = snp.getNotes()
        val notesSubscriber = TestObserver<List<Note>>()
        notes.subscribe(notesSubscriber)
        notesSubscriber.assertComplete()

        val values = notesSubscriber.values()[0]

        assertEquals("Janusz Tracz", values[0].teacher)
        assertEquals("Udział w konkursie szkolnym +20 pkt", values[0].category)
        assertEquals("+ 20p za udział w Konkursie Języka Angielskiego", values[0].content)
    }

}

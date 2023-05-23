package io.github.wulkanowy.sdk.scrapper.interceptor

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import io.github.wulkanowy.sdk.scrapper.Scrapper
import io.github.wulkanowy.sdk.scrapper.exception.ServiceUnavailableException
import io.github.wulkanowy.sdk.scrapper.exception.VulcanException
import io.github.wulkanowy.sdk.scrapper.login.LoginTest
import io.github.wulkanowy.sdk.scrapper.login.PasswordChangeRequiredException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ErrorInterceptorTest : BaseLocalTest() {

    @Test
    fun offline_databaseUpdate() {
        try {
            runBlocking { getStudentRepo(ErrorInterceptorTest::class.java, "AktualizacjaBazyDanych.html", Scrapper.LoginType.STANDARD).getNotes() }
        } catch (e: Throwable) {
            assertTrue(e is ServiceUnavailableException)
        }
    }

    @Test
    fun `eduOne update`() = runTest {
        runCatching {
            getStudentRepo(ErrorInterceptorTest::class.java, "AktualizacjaEduOne.html", Scrapper.LoginType.STANDARD).getNotes()
        }.onFailure {
            assertTrue(it is ServiceUnavailableException)
            assertEquals("Trwa aktualizacja bazy danych.", it.message)
            return@runTest
        }
        assert(false)
    }

    @Test
    fun passwordChangeRequired() {
        try {
            runBlocking { getStudentRepo(LoginTest::class.java, "PrzywracanieDostepu.html", Scrapper.LoginType.STANDARD).getNotes() }
        } catch (e: Throwable) {
            assertTrue(e is PasswordChangeRequiredException)
        }
    }

    @Test
    fun error_unknown() {
        try {
            runBlocking { getStudentRepo(ErrorInterceptorTest::class.java, "Błąd-adfs.html", Scrapper.LoginType.STANDARD).getNotes() }
        } catch (e: Throwable) {
            assertTrue(e is VulcanException)
            assertTrue(e.message?.startsWith("Błąd") == true)
        }
    }

    @Test
    fun `technical break eduOne`() = runTest {
        runCatching {
            getStudentRepo(ErrorInterceptorTest::class.java, "Przerwa.html", Scrapper.LoginType.STANDARD).getNotes()
        }.onFailure {
            assertTrue(it is ServiceUnavailableException)
            assertEquals("Przerwa", it.message)
            return@runTest
        }
        assert(false)
    }
}

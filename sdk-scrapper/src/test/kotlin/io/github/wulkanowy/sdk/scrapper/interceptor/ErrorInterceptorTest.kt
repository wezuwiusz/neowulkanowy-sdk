package io.github.wulkanowy.sdk.scrapper.interceptor

import io.github.wulkanowy.sdk.scrapper.Scrapper
import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import io.github.wulkanowy.sdk.scrapper.exception.ServiceUnavailableException
import io.github.wulkanowy.sdk.scrapper.exception.VulcanException
import io.github.wulkanowy.sdk.scrapper.login.LoginTest
import io.github.wulkanowy.sdk.scrapper.login.NotLoggedInException
import io.github.wulkanowy.sdk.scrapper.login.PasswordChangeRequiredException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class ErrorInterceptorTest : BaseLocalTest() {

    @Test
    fun notLoggedIn_standard() {
        try {
            runBlocking { getSnpRepo(LoginTest::class.java, "Logowanie-standard.html", Scrapper.LoginType.STANDARD).getNotes() }
        } catch (e: Throwable) {
            assertTrue(e is NotLoggedInException)
        }
    }

    @Test
    fun notLoggedIn_standard2() {
        try {
            runBlocking { getSnpRepo(LoginTest::class.java, "LoginPage-standard.html", Scrapper.LoginType.STANDARD, false).getNotes() }
        } catch (e: Throwable) {
            assertTrue(e is NotLoggedInException)
        }
    }

    @Test
    fun notLoggedIn_adfs() {
        try {
            runBlocking { getSnpRepo(LoginTest::class.java, "ADFS-form-2.html", Scrapper.LoginType.STANDARD).getNotes() }
        } catch (e: Throwable) {
            assertTrue(e is NotLoggedInException)
        }
    }

    @Test
    fun notLoggedIn_adfsCards() {
        try {
            runBlocking { getSnpRepo(LoginTest::class.java, "ADFS-form-1.html", Scrapper.LoginType.STANDARD).getNotes() }
        } catch (e: Throwable) {
            assertTrue(e is NotLoggedInException)
        }
    }

    @Test
    fun notLoggedInt_adfsLight() {
        try {
            runBlocking { getSnpRepo(LoginTest::class.java, "ADFSLight-form-1.html", Scrapper.LoginType.STANDARD).getNotes() }
        } catch (e: Throwable) {
            assertTrue(e is NotLoggedInException)
        }
    }

    @Test
    fun offline_databaseUpdate() {
        try {
            runBlocking { getSnpRepo(ErrorInterceptorTest::class.java, "AktualizacjaBazyDanych.html", Scrapper.LoginType.STANDARD).getNotes() }
        } catch (e: Throwable) {
            assertTrue(e is ServiceUnavailableException)
        }
    }

    @Test
    fun passwordChangeRequired() {
        try {
            runBlocking { getSnpRepo(LoginTest::class.java, "PrzywracanieDostepu.html", Scrapper.LoginType.STANDARD).getNotes() }
        } catch (e: Throwable) {
            assertTrue(e is PasswordChangeRequiredException)
        }
    }

    @Test
    fun error_unknown() {
        try {
            runBlocking { getSnpRepo(ErrorInterceptorTest::class.java, "Błąd-adfs.html", Scrapper.LoginType.STANDARD).getNotes() }
        } catch (e: Throwable) {
            assertTrue(e is VulcanException)
            assertTrue(e.message?.startsWith("Błąd") == true)
        }
    }
}

package io.github.wulkanowy.sdk.scrapper.interceptor

import io.github.wulkanowy.sdk.scrapper.Scrapper
import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import io.github.wulkanowy.sdk.scrapper.exception.ServiceUnavailableException
import io.github.wulkanowy.sdk.scrapper.exception.VulcanException
import io.github.wulkanowy.sdk.scrapper.login.LoginTest
import io.github.wulkanowy.sdk.scrapper.login.NotLoggedInException
import io.github.wulkanowy.sdk.scrapper.login.PasswordChangeRequiredException
import io.github.wulkanowy.sdk.scrapper.notes.Note
import io.reactivex.observers.TestObserver
import org.junit.Test

class ErrorInterceptorTest : BaseLocalTest() {

    @Test
    fun notLoggedIn_standard() {
        val notes = getSnpRepo(LoginTest::class.java, "Logowanie-standard.html", Scrapper.LoginType.STANDARD).getNotes()
        val observer = TestObserver<List<Note>>()
        notes.subscribe(observer)
        observer.assertError(NotLoggedInException::class.java)
    }

    @Test
    fun notLoggedIn_standard2() {
        val notes = getSnpRepo(LoginTest::class.java, "LoginPage-standard.html", Scrapper.LoginType.STANDARD).getNotes()
        val observer = TestObserver<List<Note>>()
        notes.subscribe(observer)
        observer.assertError(NotLoggedInException::class.java)
    }

    @Test
    fun notLoggedIn_adfs() {
        val notes = getSnpRepo(LoginTest::class.java, "ADFS-form-2.html", Scrapper.LoginType.ADFS).getNotes()
        val observer = TestObserver<List<Note>>()
        notes.subscribe(observer)
        observer.assertError(NotLoggedInException::class.java)
    }

    @Test
    fun notLoggedIn_adfsCards() {
        val notes = getSnpRepo(LoginTest::class.java, "ADFS-form-1.html", Scrapper.LoginType.ADFSCards).getNotes()
        val observer = TestObserver<List<Note>>()
        notes.subscribe(observer)
        observer.assertError(NotLoggedInException::class.java)
    }

    @Test
    fun notLoggedInt_adfsLight() {
        val notes = getSnpRepo(LoginTest::class.java, "ADFSLight-form-1.html", Scrapper.LoginType.ADFSLight).getNotes()
        val observer = TestObserver<List<Note>>()
        notes.subscribe(observer)
        observer.assertError(NotLoggedInException::class.java)
    }

    @Test
    fun offline_databaseUpdate() {
        val notes = getSnpRepo(ErrorInterceptorTest::class.java, "AktualizacjaBazyDanych.html", Scrapper.LoginType.STANDARD).getNotes()
        val observer = TestObserver<List<Note>>()
        notes.subscribe(observer)
        observer.assertError(ServiceUnavailableException::class.java)
    }

    @Test
    fun passwordChangeRequired() {
        val notes = getSnpRepo(LoginTest::class.java, "PrzywracanieDostepu.html", Scrapper.LoginType.ADFSLight).getNotes()
        val observer = TestObserver<List<Note>>()
        notes.subscribe(observer)
        observer.assertError(PasswordChangeRequiredException::class.java)
    }

    @Test
    fun error_unknown() {
        val notes = getSnpRepo(ErrorInterceptorTest::class.java, "Błąd-adfs.html", Scrapper.LoginType.ADFSLight).getNotes()
        val observer = TestObserver<List<Note>>()
        notes.subscribe(observer)
        observer.assertError(VulcanException::class.java)
        observer.assertError { it.message?.startsWith("Błąd") == true }
    }
}

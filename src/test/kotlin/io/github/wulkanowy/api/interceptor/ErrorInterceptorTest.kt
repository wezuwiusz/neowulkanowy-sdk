package io.github.wulkanowy.api.interceptor

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.api.BaseLocalTest
import io.github.wulkanowy.api.login.LoginTest
import io.github.wulkanowy.api.login.NotLoggedInException
import io.github.wulkanowy.api.notes.Note
import io.reactivex.observers.TestObserver
import org.junit.Test

class ErrorInterceptorTest : BaseLocalTest() {

    @Test
    fun notLoggedIn_standard() {
        val notes = getSnpRepo(LoginTest::class.java, "Logowanie-standard.html", Api.LoginType.STANDARD).getNotes()
        val observer = TestObserver<List<Note>>()
        notes.subscribe(observer)
        observer.assertError(NotLoggedInException::class.java)
    }

    @Test
    fun notLoggedIn_standard2() {
        val notes = getSnpRepo(LoginTest::class.java, "LoginPage-standard.html", Api.LoginType.STANDARD).getNotes()
        val observer = TestObserver<List<Note>>()
        notes.subscribe(observer)
        observer.assertError(NotLoggedInException::class.java)
    }

    @Test
    fun notLoggedIn_adfs() {
        val notes = getSnpRepo(LoginTest::class.java, "ADFS-form-2.html", Api.LoginType.ADFS).getNotes()
        val observer = TestObserver<List<Note>>()
        notes.subscribe(observer)
        observer.assertError(NotLoggedInException::class.java)
    }

    @Test
    fun notLoggedIn_adfsCards() {
        val notes = getSnpRepo(LoginTest::class.java, "ADFS-form-1.html", Api.LoginType.ADFSCards).getNotes()
        val observer = TestObserver<List<Note>>()
        notes.subscribe(observer)
        observer.assertError(NotLoggedInException::class.java)
    }

    @Test
    fun notLoggedInt_adfsLight() {
        val notes = getSnpRepo(LoginTest::class.java, "ADFSLight-form-1.html", Api.LoginType.ADFSLight).getNotes()
        val observer = TestObserver<List<Note>>()
        notes.subscribe(observer)
        observer.assertError(NotLoggedInException::class.java)
    }

    @Test
    fun offline_databaseUpdate() {
        val notes = getSnpRepo(ErrorInterceptorTest::class.java, "AktualizacjaBazyDanych.html", Api.LoginType.STANDARD).getNotes()
        val observer = TestObserver<List<Note>>()
        notes.subscribe(observer)
        observer.assertError(ServiceUnavailableException::class.java)
    }
}

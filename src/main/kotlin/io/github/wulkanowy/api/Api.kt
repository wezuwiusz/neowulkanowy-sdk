package io.github.wulkanowy.api

import io.github.wulkanowy.api.auth.Client
import io.github.wulkanowy.api.auth.Login
import io.github.wulkanowy.api.auth.NotLoggedInErrorException
import io.github.wulkanowy.api.interceptor.LoginInterceptor
import io.github.wulkanowy.api.repository.StudentAndParentRepository
import okhttp3.logging.HttpLoggingInterceptor
import java.net.CookieManager
import java.net.CookiePolicy

class Api {

    var logLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BASIC

    var holdSession: Boolean = true

    var ssl: Boolean = true

    var host: String = "fakelog.cf"

    var symbol: String = "Default"

    lateinit var email: String

    lateinit var password: String

    lateinit var schoolId: String

    lateinit var studentId: String

    lateinit var diaryId: String

    private val cookies by lazy {
        val cookieManager = CookieManager()
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        cookieManager
    }

    private val clientBuilder by lazy {
        ClientCreator(cookies).addInterceptor(HttpLoggingInterceptor().setLevel(logLevel))
    }

    private val login by lazy {
        if (!::email.isInitialized || !::password.isInitialized) throw NotLoggedInErrorException("Email or/and password are not set")
        Login(email, password, symbol, Client(cookies, host, logLevel))
    }

    private val loginInterceptor by lazy {
        if (!::studentId.isInitialized || !::diaryId.isInitialized) throw NotLoggedInErrorException("Student or/and diary id are not set")
        LoginInterceptor(host, diaryId, studentId, login, holdSession)
    }

    private val snp by lazy {
        if (!::schoolId.isInitialized) throw NotLoggedInErrorException("School ID is not set")
        StudentAndParentRepository(ssl, host, symbol, schoolId, clientBuilder.addInterceptor(loginInterceptor).getClient())
    }

    fun getAttendance(startDate: String) = snp.getAttendance(startDate)

    fun getExams(startDate: String) = snp.getExams(startDate)

    fun getGrades(semester: Int) = snp.getGrades(semester)

    fun getGradesSummary(semester: Int) = snp.getGradesSummary(semester)

    fun getHomework(date: String) = snp.getHomework(date)

    fun getNotes() = snp.getNotes()
}

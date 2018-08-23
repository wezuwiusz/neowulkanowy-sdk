package io.github.wulkanowy.api

import io.github.wulkanowy.api.auth.NotLoggedInException
import io.github.wulkanowy.api.interceptor.ErrorInterceptor
import io.github.wulkanowy.api.interceptor.LoginInterceptor
import io.github.wulkanowy.api.interceptor.StudentAndParentInterceptor
import io.github.wulkanowy.api.repository.LoginRepository
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
        ClientCreator(cookies)
                .addInterceptor(HttpLoggingInterceptor().setLevel(logLevel))
                .addInterceptor(ErrorInterceptor())
    }

    private val loginRepository by lazy {
        LoginRepository(ssl, host, symbol, clientBuilder.getClient())
    }

    private val loginInterceptor by lazy {
        if (!::email.isInitialized || !::password.isInitialized) throw NotLoggedInException("Email or/and password are not set")
        if (!::studentId.isInitialized || !::diaryId.isInitialized) throw NotLoggedInException("Student or/and diary id are not set")
        LoginInterceptor(loginRepository, holdSession, email, password)
    }

    private val studentAndParentInterceptor by lazy {
        StudentAndParentInterceptor(cookies, host, diaryId, studentId)
    }

    private val snp by lazy {
        if (!::schoolId.isInitialized) throw NotLoggedInException("School ID is not set")
        StudentAndParentRepository(ssl, host, symbol, schoolId, clientBuilder
                .addInterceptor(loginInterceptor)
                .addInterceptor(studentAndParentInterceptor)
                .getClient())
    }

    fun getAttendance(startDate: String) = snp.getAttendance(startDate)

    fun getExams(startDate: String) = snp.getExams(startDate)

    fun getGrades(semester: Int) = snp.getGrades(semester)

    fun getGradesSummary(semester: Int) = snp.getGradesSummary(semester)

    fun getHomework(date: String) = snp.getHomework(date)

    fun getNotes() = snp.getNotes()

    fun getStudentInfo() = snp.getStudentInfo()
}

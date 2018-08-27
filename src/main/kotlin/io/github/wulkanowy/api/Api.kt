package io.github.wulkanowy.api

import io.github.wulkanowy.api.auth.NotLoggedInException
import io.github.wulkanowy.api.interceptor.ErrorInterceptor
import io.github.wulkanowy.api.interceptor.LoginInterceptor
import io.github.wulkanowy.api.interceptor.StudentAndParentInterceptor
import io.github.wulkanowy.api.repository.LoginRepository
import io.github.wulkanowy.api.repository.RegisterRepository
import io.github.wulkanowy.api.repository.StudentAndParentRepository
import io.github.wulkanowy.api.repository.StudentAndParentStartRepository
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

    private val schema by lazy { "http" + if (ssl) "s" else "" }

    private val cookies by lazy {
        val cookieManager = CookieManager()
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        cookieManager
    }

    private val onChangeManager = resettableManager()

    fun onConfigChange() = onChangeManager.reset()

    private val clientBuilder by resettableLazy(onChangeManager) {
        ClientCreator(cookies)
                .addInterceptor(HttpLoggingInterceptor().setLevel(logLevel))
                .addInterceptor(ErrorInterceptor())
    }

    private val loginRepository by resettableLazy(onChangeManager) {
        LoginRepository(schema, host, symbol, clientBuilder.getClient())
    }

    private val loginInterceptor by resettableLazy(onChangeManager) {
        if (!::email.isInitialized || !::password.isInitialized) throw NotLoggedInException("Email or/and password are not set")
        LoginInterceptor(loginRepository, holdSession, email, password)
    }

    private val studentAndParentInterceptor by resettableLazy(onChangeManager) {
        if (!::diaryId.isInitialized || !::studentId.isInitialized) throw NotLoggedInException("Student or/and diaryId id are not set")
        StudentAndParentInterceptor(cookies, schema, host, diaryId, studentId)
    }

    private val snp by resettableLazy(onChangeManager) {
        if (!::schoolId.isInitialized) throw NotLoggedInException("School ID is not set")
        StudentAndParentRepository(schema, host, symbol, schoolId, clientBuilder
                .addInterceptor(loginInterceptor)
                .addInterceptor(studentAndParentInterceptor)
                .getClient())
    }

    private val register by resettableLazy(onChangeManager) {
        if (!::email.isInitialized || !::password.isInitialized) throw NotLoggedInException("Email or/and password are not set")
        RegisterRepository(symbol, email, password, loginRepository)
    }

    private val studentAndParentStartRepository by resettableLazy(onChangeManager) {
        if (!::schoolId.isInitialized || !::studentId.isInitialized) throw NotLoggedInException("School or/and student id are not set")
        StudentAndParentStartRepository(schema, host, symbol, schoolId, studentId, cookies, clientBuilder.addInterceptor(loginInterceptor))
    }

    fun getPupils() = register.getPupils()

    fun getSemesters() = studentAndParentStartRepository.getSemesters()

    fun getAttendance(startDate: String) = snp.getAttendance(startDate)

    fun getExams(startDate: String) = snp.getExams(startDate)

    fun getGrades(semester: Int) = snp.getGrades(semester)

    fun getGradesSummary(semester: Int) = snp.getGradesSummary(semester)

    fun getHomework(date: String) = snp.getHomework(date)

    fun getNotes() = snp.getNotes()

    fun getStudentInfo() = snp.getStudentInfo()
}

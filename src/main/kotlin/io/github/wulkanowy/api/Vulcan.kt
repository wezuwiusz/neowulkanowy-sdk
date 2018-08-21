package io.github.wulkanowy.api

import io.github.wulkanowy.api.auth.Client
import io.github.wulkanowy.api.auth.Login
import io.github.wulkanowy.api.interceptor.LoginInterceptor
import io.github.wulkanowy.api.repository.StudentAndParentRepository
import okhttp3.logging.HttpLoggingInterceptor
import java.net.CookieManager
import java.net.CookiePolicy

class Vulcan(
        logLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BODY,
        holdSession: Boolean = true,
        ssl: Boolean = true,
        host: String = "fakelog.cf",
        symbol: String = "Default",

        email: String,
        password: String,

        schoolId: String,
        studentId: String,
        diaryId: String
) {

    private val cookies by lazy {
        val cookieManager = CookieManager()
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        cookieManager
    }

    private val clientBuilder = ClientCreator(cookies).addInterceptor(HttpLoggingInterceptor().setLevel(logLevel))

    private val login by lazy { Login(email, password, symbol, Client(cookies, host)) }

    private val loginInterceptor by lazy { LoginInterceptor(host, diaryId, studentId, login, holdSession) }

    private val snp by lazy {
        StudentAndParentRepository(ssl, host, symbol, schoolId, clientBuilder.addInterceptor(loginInterceptor).getClient())
    }

    fun getAttendance(startDate: String) = snp.getAttendance(startDate)

    fun getExams(startDate: String) = snp.getExams(startDate)

    fun getGrades(semester: Int) = snp.getGrades(semester)

    fun getNotes() = snp.getNotes()
}

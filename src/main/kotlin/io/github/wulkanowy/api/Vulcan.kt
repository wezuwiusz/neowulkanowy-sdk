package io.github.wulkanowy.api

import io.github.wulkanowy.api.interceptor.LoginInterceptor
import io.github.wulkanowy.api.repository.StudentAndParentRepository
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.net.CookieManager
import java.net.CookiePolicy

class Vulcan(
        private val logLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BODY,
        private val holdSession: Boolean = true,
        private val ssl: Boolean = true,
        private val host: String = "fakelog.cf",
        private val symbol: String = "Default",

        private val email: String,
        private val password: String,

        private val schoolId: String,
        private val studentId: String,
        private val diaryId: String
) {

    private val cookies by lazy {
        val cookieManager = CookieManager()
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        cookieManager
    }

    private val client by lazy {
        OkHttpClient().newBuilder()
                .cookieJar(JavaNetCookieJar(cookies))
                .addInterceptor(LoginInterceptor(email, password, symbol, host, diaryId, studentId, cookies, holdSession))
                .addInterceptor(HttpLoggingInterceptor().setLevel(logLevel))
                .build()
    }

    private val snp by lazy {
        StudentAndParentRepository(ssl, host, symbol, schoolId, client)
    }

    fun getAttendance(startDate: String) = snp.getAttendance(startDate)

    fun getExams(startDate: String) = snp.getExams(startDate)

    fun getGrades(semester: Int) = snp.getGrades(semester)

    fun getNotes() = snp.getNotes()
}

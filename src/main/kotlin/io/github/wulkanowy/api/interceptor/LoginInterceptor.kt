package io.github.wulkanowy.api.interceptor

import io.github.wulkanowy.api.repository.LoginRepository
import okhttp3.Interceptor
import okhttp3.Response
import java.util.*
import java.util.concurrent.TimeUnit

class LoginInterceptor(
        private var loginRepo: LoginRepository,
        private val holdSession: Boolean,
        private val email: String,
        private val password: String
) : Interceptor {

    companion object {
        const val MAX_SESSION_TIME = 5
    }

    private var lastSuccessRequest: Date? = null

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isLoggedIn() || !holdSession) {
            loginRepo.login(email, password).subscribe()
            lastSuccessRequest = Date()
        }

        return chain.proceed(chain.request().newBuilder().build())
    }

    private fun isLoggedIn() = lastSuccessRequest != null
            && MAX_SESSION_TIME > TimeUnit.MILLISECONDS.toMinutes(Date().time - lastSuccessRequest!!.time)
}

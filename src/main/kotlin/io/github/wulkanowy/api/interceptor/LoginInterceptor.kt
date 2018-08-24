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
            login()
            lastSuccessRequest = Date()
        }

        return chain.proceed(chain.request().newBuilder().build())
    }

    private fun isLoggedIn() = lastSuccessRequest != null
            && MAX_SESSION_TIME > TimeUnit.MILLISECONDS.toMinutes(Date().time - lastSuccessRequest!!.time)

    private fun login() {
        if (loginRepo.isADFS()) loginADFS()
        else loginNormal()
    }

    private fun loginNormal() {
        loginRepo.sendCredentials(mapOf(
                "LoginName" to email,
                "Password" to password)
        ).flatMap {
            loginRepo.sendCertificate(it)
        }.subscribe()
    }

    private fun loginADFS() {
        loginRepo.getADFSFormState().flatMap {
            loginRepo.sendADFSFormStandardChoice(it.formAction, mapOf(
                    "__VIEWSTATE" to it.viewstate,
                    "__VIEWSTATEGENERATOR" to it.viewstateGenerator,
                    "__EVENTVALIDATION" to it.eventValidation,
                    "__db" to it.db,
                    "PassiveSignInButton.x" to "0",
                    "PassiveSignInButton.y" to "0"
            ))
        }.flatMap {
            loginRepo.sendADFSCredentials(it.formAction, mapOf(
                    "__db" to it.db,
                    "__EVENTVALIDATION" to it.eventValidation,
                    "__VIEWSTATE" to it.viewstate,
                    "__VIEWSTATEGENERATOR" to it.viewstateGenerator,
                    "SubmitButton.x" to "0",
                    "SubmitButton.y" to "0",
                    "UsernameTextBox" to email,
                    "PasswordTextBox" to password
            ))
        }.flatMap {
            loginRepo.sendCertificate(it)
        }.flatMap {
            loginRepo.sendCertificate(it)
        }.subscribe()
    }
}

package io.github.wulkanowy.api.interceptor

import io.github.wulkanowy.api.repository.LoginRepository
import okhttp3.Interceptor
import okhttp3.Response

class LoginInterceptor(
        private var loginRepo: LoginRepository,
        private val holdSession: Boolean,
        private val email: String,
        private val password: String
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isLoggedIn() || !holdSession) login()

        return chain.proceed(chain.request().newBuilder().build())
    }

    private fun isLoggedIn() = false

    private fun login() {
        if (loginRepo.isADFS()) loginADFS()
        else loginNormal()
    }

    private fun loginNormal() {
        loginRepo.sendCertificate(loginRepo.sendCredentials(mapOf(
                "LoginName" to email,
                "Password" to password)
        ).blockingGet()).blockingGet()
    }

    private fun loginADFS() {
        val state1 = loginRepo.getADFSFormState().blockingGet()

        val state2 = loginRepo.sendADFSFormStandardChoice(state1.formAction, mapOf(
                "__VIEWSTATE" to state1.viewstate,
                "__VIEWSTATEGENERATOR" to state1.viewstateGenerator,
                "__EVENTVALIDATION" to state1.eventValidation,
                "__db" to state1.db,
                "PassiveSignInButton.x" to "0",
                "PassiveSignInButton.y" to "0"
        )).blockingGet()

        loginRepo.sendCertificate(loginRepo.sendCertificate(loginRepo.sendADFSCredentials(state2.formAction, mapOf(
                "__db" to state2.db,
                "__EVENTVALIDATION" to state2.eventValidation,
                "__VIEWSTATE" to state2.viewstate,
                "__VIEWSTATEGENERATOR" to state2.viewstateGenerator,
                "SubmitButton.x" to "0",
                "SubmitButton.y" to "0",
                "UsernameTextBox" to email,
                "PasswordTextBox" to password
        )).blockingGet()).blockingGet()).blockingGet()
    }
}

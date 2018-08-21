package io.github.wulkanowy.api.interceptor

import io.github.wulkanowy.api.auth.Login
import okhttp3.Interceptor
import okhttp3.Response
import java.net.HttpCookie
import java.net.URI

class LoginInterceptor(private val host: String,
                       private val diaryId: String,
                       private val studentId: String,
                       private var login: Login,
                       private val holdSession: Boolean
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain?): Response {
        if (!login.isLoggedIn() || !holdSession) {
            login.login()

            arrayOf(
                    arrayOf("idBiezacyDziennik", diaryId),
                    arrayOf("idBiezacyUczen", studentId)
            ).forEach {
                val cookie = HttpCookie(it[0], it[1])
                cookie.path = "/"
                cookie.domain = "uonetplus-opiekun.$host"
                login.client.addCookie(URI("${cookie.domain}.$host"), cookie)
            }
        }

        return chain!!.proceed(chain.request().newBuilder().build())
    }
}

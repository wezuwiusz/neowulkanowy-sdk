package io.github.wulkanowy.api.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.net.CookieManager
import java.net.HttpCookie
import java.net.URI

class StudentAndParentInterceptor(
        private val cookies: CookieManager,
        private val schema: String,
        private val host: String,
        private val diaryId: String,
        private val studentId: String
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        arrayOf(
                arrayOf("idBiezacyDziennik", diaryId),
                arrayOf("idBiezacyUczen", studentId)
        ).forEach {
            val cookie = HttpCookie(it[0], it[1])
            cookie.path = "/"
            cookie.domain = "$schema://uonetplus-opiekun.$host"
            cookies.cookieStore.add(URI(cookie.domain), cookie)
        }

        return chain.proceed(chain.request().newBuilder().build())
    }
}

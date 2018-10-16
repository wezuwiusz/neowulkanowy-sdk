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
        private val diaryId: Int,
        private val studentId: Int
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        arrayOf(
                arrayOf("idBiezacyDziennik", diaryId),
                arrayOf("idBiezacyUczen", studentId)
        ).forEach { cookie ->
            HttpCookie(cookie[0].toString(), cookie[1].toString()).let {
                it.path = "/"
                it.domain = "uonetplus-opiekun.$host"
                cookies.cookieStore.add(URI("$schema://${it.domain}"), it)
            }
        }

        return chain.proceed(chain.request().newBuilder().build())
    }
}

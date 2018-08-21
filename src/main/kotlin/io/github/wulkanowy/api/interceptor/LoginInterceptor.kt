package io.github.wulkanowy.api.interceptor

import io.github.wulkanowy.api.auth.Client
import io.github.wulkanowy.api.auth.Login
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import java.net.CookieManager
import java.net.HttpCookie
import java.net.URI

class LoginInterceptor(private val email: String,
                       private val password: String,
                       private val symbol: String,
                       private val host: String,
                       private val diaryId: String,
                       private val studentId: String,
                       private var cookies: CookieManager,
                       private val holdSession: Boolean
) : Interceptor {

    private val login = Login(ClientImpl(cookies, host))

    override fun intercept(chain: Interceptor.Chain?): Response {
        if (!login.isLoggedIn() || !holdSession) {
            login.login(email, password, symbol)

            arrayOf(
                    arrayOf("idBiezacyDziennik", diaryId),
                    arrayOf("idBiezacyUczen", studentId)
            ).forEach {
                val cookie = HttpCookie(it[0], it[1])
                cookie.path = "/"
                cookie.domain = "uonetplus-opiekun.$host"
                cookies.cookieStore.add(URI("${cookie.domain}.$host"), cookie)
            }
        }

        return chain!!.proceed(chain.request().newBuilder().build())
    }

    class ClientImpl(private val cookies: CookieManager, private val host: String) : Client {

        private var symbol = "Default"

        private val client by lazy {
            OkHttpClient.Builder()
                    .cookieJar(JavaNetCookieJar(cookies))
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build()
        }

        override fun clearCookies() {
            cookies.cookieStore.removeAll()
        }

        override fun getCookies(): MutableList<HttpCookie> {
            return cookies.cookieStore.cookies
        }

        override fun getPageByUrl(url: String): Document {
            val request = Request.Builder()
                    .url(getUrl(url))
                    .build()

            val response = client.newCall(request).execute()
            return Jsoup.parse(response.body()!!.string())
        }

        override fun postPageByUrl(url: String, formParams: Array<Array<String>>): Document {
            val formBody = FormBody.Builder()

            formParams.forEach {
                formBody.add(it[0], it[1])
            }

            val request = Request.Builder()
                    .url(getUrl(url))
                    .post(formBody.build())
                    .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            return Jsoup.parse(response.body()!!.string())
        }

        private fun getUrl(url: String): String {
            return url
                    .replace("{schema}", "https")
                    .replace("{host}", host)
                    .replace("{symbol}", symbol)
        }

        override fun setSymbol(symbol: String) {
            this.symbol = symbol
        }

    }
}

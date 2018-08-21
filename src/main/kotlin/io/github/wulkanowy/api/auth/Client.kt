package io.github.wulkanowy.api.auth

import okhttp3.FormBody
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import java.net.CookieManager
import java.net.HttpCookie
import java.net.URI

class Client(private val cookies: CookieManager, private val host: String, private val logLvl: HttpLoggingInterceptor.Level) {

    private var symbol = "Default"

    private val client by lazy {
        OkHttpClient.Builder()
                .cookieJar(JavaNetCookieJar(cookies))
                .addInterceptor(HttpLoggingInterceptor().setLevel(logLvl))
                .build()
    }

    fun clearCookies() {
        cookies.cookieStore.removeAll()
    }

    fun getCookies(): MutableList<HttpCookie> {
        return cookies.cookieStore.cookies
    }

    fun addCookie(uri: URI, cookie: HttpCookie) {
        cookies.cookieStore.add(uri, cookie)
    }

    fun getPageByUrl(url: String): Document {
        val request = Request.Builder()
                .url(getUrl(url))
                .build()

        val response = client.newCall(request).execute()
        return Jsoup.parse(response.body()!!.string())
    }

    fun postPageByUrl(url: String, formParams: Array<Array<String>>): Document {
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

    fun setSymbol(symbol: String) {
        this.symbol = symbol
    }

    private fun getUrl(url: String): String {
        return url
                .replace("{schema}", "https")
                .replace("{host}", host)
                .replace("{symbol}", symbol)
    }
}

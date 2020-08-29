package io.github.wulkanowy.sdk.scrapper.interceptor

import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFS
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFSCards
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFSLight
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFSLightCufs
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFSLightScoped
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.STANDARD
import io.github.wulkanowy.sdk.scrapper.login.NotLoggedInException
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository.Companion.SELECTOR_ADFS
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository.Companion.SELECTOR_ADFS_CARDS
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository.Companion.SELECTOR_ADFS_LIGHT
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository.Companion.SELECTOR_STANDARD
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.net.CookieManager

class AutoLoginInterceptor(
    private val loginType: LoginType,
    private val jar: CookieManager,
    private val emptyCookieJarIntercept: Boolean,
    private val notLoggedInCallback: () -> Boolean
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request
        val response: Response

        // synchronized(jar) {
            try {
                request = chain.request()
                checkRequest()
                response = chain.proceed(request)
                checkResponse(
                    doc = Jsoup.parse(response.peekBody(Long.MAX_VALUE).string()),
                    url = chain.request().url().toString()
                )
            } catch (e: NotLoggedInException) {
                println("Not logged in. Login in...")
                if (notLoggedInCallback()) {
                    println("Retry after login...")
                    return chain.proceed(chain.request().newBuilder().build())
                } else throw e
            }
        // }

        return response
    }

    private fun checkRequest() {
        if (emptyCookieJarIntercept && jar.cookieStore.cookies.isEmpty()) {
            throw NotLoggedInException("No cookie found! You are not logged in yet")
        }
    }

    private fun checkResponse(doc: Document, url: String) {
        // if (chain.request().url().toString().contains("/Start.mvc/Get")) {
        if (url.contains("/Start.mvc/")) { // /Index return error too in 19.09.0000.34977
            doc.select(".errorBlock").let {
                if (it.isNotEmpty()) throw NotLoggedInException(it.select(".errorTitle").text())
            }
        }

        if (when (loginType) {
                STANDARD -> doc.select(SELECTOR_STANDARD)
                ADFS -> doc.select(SELECTOR_ADFS)
                ADFSLight, ADFSLightCufs, ADFSLightScoped -> doc.select(SELECTOR_ADFS_LIGHT)
                ADFSCards -> doc.select(SELECTOR_ADFS_CARDS)
                else -> Elements()
            }.isNotEmpty()
        ) {
            throw NotLoggedInException("User not logged in")
        }

        doc.body().text().let {
            // /messages
            if (it.contains("The custom error module")) throw NotLoggedInException("Zaloguj się")
        }
    }
}

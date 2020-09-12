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
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.slf4j.LoggerFactory
import java.net.CookieManager
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class AutoLoginInterceptor(
    private val loginType: LoginType,
    private val jar: CookieManager,
    private val emptyCookieJarIntercept: Boolean,
    private val notLoggedInCallback: suspend () -> Unit
) : Interceptor {

    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    private val lock: Lock = ReentrantLock()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request
        val response: Response

        try {
            request = chain.request()
            checkRequest()
            response = chain.proceed(request)
            checkResponse(
                doc = Jsoup.parse(response.peekBody(Long.MAX_VALUE).string()),
                url = chain.request().url().toString()
            )
        } catch (e: NotLoggedInException) {
            if (lock.tryLock()) {
                logger.warn("Not logged in. Login in...")
                try {
                    runBlocking { notLoggedInCallback() }
                    return chain.proceed(chain.request().newBuilder().build())
                } catch (e: Throwable) {
                    logger.warn("Error occurred on login")
                    throw e
                } finally {
                    logger.warn("Login finished. Release lock")
                    lock.unlock()
                }
            } else {
                logger.warn("Wait for user to be logged in...")
                lock.lock()
                lock.unlock()
                logger.warn("User logged in. Retry after login...")

                return chain.proceed(chain.request().newBuilder().build())
            }
        }

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

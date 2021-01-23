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
import okhttp3.MediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.slf4j.LoggerFactory
import retrofit2.HttpException
import java.io.IOException
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
            if (response.body()?.contentType()?.subtype() != "json") {
                val body = response.peekBody(Long.MAX_VALUE).byteStream()
                val url = chain.request().url().toString()
                checkResponse(Jsoup.parse(body, null, url), url)
            }
        } catch (e: NotLoggedInException) {
            if (lock.tryLock()) {
                logger.debug("Not logged in. Login in...")
                return try {
                    runBlocking { notLoggedInCallback() }
                    chain.proceed(chain.request().newBuilder().build())
                } catch (e: IOException) {
                    logger.debug("Error occurred on login")
                    throw e
                } catch (e: HttpException) {
                    logger.debug("Error occurred on login")
                    e.toOkHttpResponse(chain.request())
                } catch (e: Throwable) {
                    throw IOException("Unknown exception on login", e)
                } finally {
                    logger.debug("Login finished. Release lock")
                    lock.unlock()
                }
            } else {
                logger.debug("Wait for user to be logged in...")
                lock.lock()
                lock.unlock()
                logger.debug("User logged in. Retry after login...")

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

    /**
     * @see [https://github.com/square/retrofit/issues/3110#issuecomment-536248102]
     */
    private fun HttpException.toOkHttpResponse(request: Request) = Response.Builder()
        .code(code())
        .message(message())
        .request(request)
        .protocol(Protocol.HTTP_1_1)
        .body(response()?.errorBody() ?: object : ResponseBody() {
            override fun contentLength() = 0L
            override fun contentType(): MediaType? = null
            override fun source(): BufferedSource = Buffer()
        })
        .build()
}

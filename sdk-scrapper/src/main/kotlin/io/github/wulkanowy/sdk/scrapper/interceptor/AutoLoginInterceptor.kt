package io.github.wulkanowy.sdk.scrapper.interceptor

import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFS
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFSCards
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFSLight
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFSLightCufs
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFSLightScoped
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.STANDARD
import io.github.wulkanowy.sdk.scrapper.exception.VulcanClientError
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
import okio.withLock
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.slf4j.LoggerFactory
import retrofit2.HttpException
import java.io.IOException
import java.net.CookieManager
import java.net.HttpURLConnection
import java.util.concurrent.locks.ReentrantLock

private val lock = ReentrantLock()

internal class AutoLoginInterceptor(
    private val loginType: LoginType,
    private val jar: CookieManager,
    private val emptyCookieJarIntercept: Boolean = false,
    private val notLoggedInCallback: suspend () -> Unit,
    private val fetchStudentCookies: suspend () -> Unit,
    private val fetchMessagesCookies: suspend () -> Unit,
) : Interceptor {

    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request
        val response: Response
        val uri = chain.request().url
        val url = uri.toString()

        try {
            request = chain.request()
            checkRequest()
            response = try {
                chain.proceed(request)
            } catch (e: Throwable) {
                if (e is VulcanClientError) {
                    checkHttpErrorResponse(e, url)
                }
                throw e
            }
            if (response.body?.contentType()?.subtype != "json") {
                val body = response.peekBody(Long.MAX_VALUE).byteStream()
                checkResponse(Jsoup.parse(body, null, url), url)
            }
        } catch (e: NotLoggedInException) {
            lock.withLock {
                logger.debug("Not logged in. Login in...")
                return try {
                    runBlocking {
                        notLoggedInCallback()
                        val messagesRes = runCatching { fetchMessagesCookies() }
                            .onFailure { logger.error("Error in messages cookies fetch", it) }
                        val studentRes = runCatching { fetchStudentCookies() }
                            .onFailure { logger.error("Error in student cookies fetch", it) }
                        when {
                            "wiadomosciplus" in uri.host -> messagesRes.getOrThrow()
                            "uczen" in uri.host -> studentRes.getOrThrow()
                        }
                    }
                    chain.proceed(chain.request().newBuilder().build())
                } catch (e: IOException) {
                    logger.debug("Error occurred on login")
                    throw e
                } catch (e: HttpException) {
                    logger.debug("Error occurred on login")
                    e.toOkHttpResponse(chain.request())
                } catch (e: Throwable) {
                    throw IOException("Unknown exception on login", e)
                }
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

        val loginSelectors = when (loginType) {
            STANDARD -> doc.select(SELECTOR_STANDARD)
            ADFS -> doc.select(SELECTOR_ADFS)
            ADFSLight, ADFSLightCufs, ADFSLightScoped -> doc.select(SELECTOR_ADFS_LIGHT)
            ADFSCards -> doc.select(SELECTOR_ADFS_CARDS)
            else -> Elements()
        }
        if (loginSelectors.isNotEmpty()) {
            throw NotLoggedInException("User not logged in")
        }

        val bodyContent = doc.body().text()
        when {
            // uonetplus-uczen
            "The custom error module" in bodyContent -> {
                throw NotLoggedInException(bodyContent)
            }
        }
    }

    private fun checkHttpErrorResponse(error: VulcanClientError, url: String) {
        if (error.httpCode == HttpURLConnection.HTTP_CONFLICT && "uonetplus-wiadomosciplus" in url) {
            throw NotLoggedInException(error.message.orEmpty())
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
        .body(
            body = response()?.errorBody() ?: object : ResponseBody() {
                override fun contentLength() = 0L

                override fun contentType(): MediaType? = null

                override fun source(): BufferedSource = Buffer()
            },
        )
        .build()
}

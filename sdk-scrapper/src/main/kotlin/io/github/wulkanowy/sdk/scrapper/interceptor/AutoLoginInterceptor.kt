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
import okhttp3.Cookie
import okhttp3.Interceptor
import okhttp3.JavaNetCookieJar
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
import java.net.HttpURLConnection
import java.util.concurrent.locks.ReentrantLock

private val lock = ReentrantLock(true)

internal class AutoLoginInterceptor(
    private val loginType: LoginType,
    private val jar: CookieManager,
    private val emptyCookieJarIntercept: Boolean = false,
    private val notLoggedInCallback: suspend () -> Unit,
    private val fetchStudentCookies: () -> Unit,
    private val fetchMessagesCookies: () -> Unit,
) : Interceptor {

    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    private val cookieJar = JavaNetCookieJar(jar)

    @Volatile
    private var lastError: Throwable? = null

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
            return if (lock.tryLock()) {
                logger.debug("Not logged in. Login in...")
                try {
                    runBlocking { notLoggedInCallback() }
                    val messages = runCatching { fetchMessagesCookies() }
                        .onFailure { it.printStackTrace() }
                    val student = runCatching { fetchStudentCookies() }
                        .onFailure { it.printStackTrace() }
                    when {
                        "wiadomosciplus" in uri.host -> messages.getOrThrow()
                        "uczen" in uri.host -> student.getOrThrow()
                        else -> logger.info("Resource don't need further login")
                    }
                    chain.retryRequest()
                } catch (e: IOException) {
                    logger.debug("Error occurred on login")
                    lastError = e
                    throw e
                } catch (e: HttpException) {
                    logger.debug("Error occurred on login")
                    lastError = e
                    e.toOkHttpResponse(chain.request())
                } catch (e: Throwable) {
                    lastError = e
                    throw IOException("Unknown exception on login", e)
                } finally {
                    logger.debug("Login finished. Release lock")
                    lock.unlock()
                }
            } else {
                try {
                    logger.debug("Wait for user to be logged in...")
                    lock.lock()
                    lastError?.let {
                        throw it
                    } ?: logger.warn("There is no last exception")
                } finally {
                    lock.unlock()
                    logger.debug("User logged in. Retry after login...")
                }

                chain.retryRequest()
            }
        }

        return response
    }

    private fun Interceptor.Chain.retryRequest(): Response {
        Thread.sleep(10)

        val newRequest = request()
            .newBuilder()
            .header("Cookie", cookieJar.loadForRequest(request().url).cookieHeader())
            .build()

        return proceed(newRequest)
    }

    private fun List<Cookie>.cookieHeader(): String = buildString {
        this@cookieHeader.forEachIndexed { index, cookie ->
            if (index > 0) append("; ")
            append(cookie.name).append('=').append(cookie.value)
        }
    }

    /**
     * @see [okhttp3.internal.http.BridgeInterceptor]
     */
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

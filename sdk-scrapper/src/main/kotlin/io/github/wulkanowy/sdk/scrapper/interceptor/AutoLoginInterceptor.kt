package io.github.wulkanowy.sdk.scrapper.interceptor

import io.github.wulkanowy.sdk.scrapper.ApiEndpoints
import io.github.wulkanowy.sdk.scrapper.ApiResponse
import io.github.wulkanowy.sdk.scrapper.CookieJarCabinet
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFS
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFSCards
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFSLight
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFSLightCufs
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFSLightScoped
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.STANDARD
import io.github.wulkanowy.sdk.scrapper.attachVToken
import io.github.wulkanowy.sdk.scrapper.exception.VulcanClientError
import io.github.wulkanowy.sdk.scrapper.getScriptParam
import io.github.wulkanowy.sdk.scrapper.login.LoginResult
import io.github.wulkanowy.sdk.scrapper.login.ModuleHeaders
import io.github.wulkanowy.sdk.scrapper.login.NotLoggedInException
import io.github.wulkanowy.sdk.scrapper.login.UrlGenerator
import io.github.wulkanowy.sdk.scrapper.mapModuleUrls
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository.Companion.SELECTOR_ADFS
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository.Companion.SELECTOR_ADFS_CARDS
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository.Companion.SELECTOR_ADFS_LIGHT
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository.Companion.SELECTOR_STANDARD
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
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
import java.net.HttpURLConnection
import java.util.concurrent.locks.ReentrantLock

internal const val MessagesModuleHost = "uonetplus-wiadomosciplus"
internal const val StudentPlusModuleHost = "uonetplus-uczenplus"
internal const val StudentModuleHost = "uonetplus-uczen"

internal class AutoLoginInterceptor(
    private val loginType: LoginType,
    private val cookieJarCabinet: CookieJarCabinet,
    private val emptyCookieJarIntercept: Boolean = false,
    private val notLoggedInCallback: suspend () -> LoginResult,
    private val fetchModuleCookies: (UrlGenerator.Site) -> Pair<HttpUrl, Document>,
    private val json: Json,
    private val headersByHost: MutableMap<String, ModuleHeaders?> = mutableMapOf(),
    private val loginLock: ReentrantLock = ReentrantLock(true),
) : Interceptor {

    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val uri = chain.request().url
        val url = uri.toString()

        return try {
            val request = chain.request()
            checkRequest()
            val response = try {
                chain.proceed(request.attachModuleHeaders())
            } catch (e: Throwable) {
                if (e is VulcanClientError) {
                    checkHttpErrorResponse(e, url)
                }
                throw e
            }
            if (response.body?.contentType()?.subtype != "json") {
                val body = response.peekBody(Long.MAX_VALUE).byteStream()
                val html = Jsoup.parse(body, null, url)
                checkResponse(html, url, response)
                saveModuleHeaders(html, uri)
            }
            response
        } catch (e: NotLoggedInException) {
            if (loginLock.tryLock()) {
                logger.debug("Not logged in. Login in...")
                try {
                    val loginResult = runBlocking { notLoggedInCallback() }

                    headersByHost[MessagesModuleHost] = null
                    headersByHost[StudentPlusModuleHost] = null
                    headersByHost[StudentModuleHost] = null

                    val messages = getModuleCookies(UrlGenerator.Site.MESSAGES)
                    val student = when (loginResult.isStudentSchoolUseEduOne) {
                        true -> getModuleCookies(UrlGenerator.Site.STUDENT_PLUS)
                        else -> getModuleCookies(UrlGenerator.Site.STUDENT)
                    }

                    when {
                        MessagesModuleHost in uri.host -> messages.getOrThrow()
                        StudentPlusModuleHost in uri.host -> student.getOrThrow()
                        StudentModuleHost in uri.host -> student.getOrThrow()
                        else -> logger.info("Resource don't need further login anyway")
                    }
                    chain.proceed(chain.request().attachModuleHeaders())
                } catch (e: IOException) {
                    logger.debug("IO Error occurred on login")
                    throw e
                } catch (e: HttpException) {
                    logger.debug("HTTP Error occurred on login")
                    e.toOkHttpResponse(chain.request())
                } catch (e: Throwable) {
                    throw IOException("Unknown exception on login", e)
                } finally {
                    logger.debug("Login finished. Release lock")
                    loginLock.unlock()
                }
            } else {
                try {
                    logger.debug("Wait for user to be logged in...")
                    loginLock.lock()
                } finally {
                    loginLock.unlock()
                    logger.debug("User logged in. Retry after login...")
                }

                chain.proceed(chain.request().attachModuleHeaders())
            }
        }
    }

    private fun getModuleCookies(site: UrlGenerator.Site): Result<Pair<HttpUrl, Document>> {
        return runCatching { fetchModuleCookies(site) }
            .onFailure { logger.error("Error in $site login", it) }
            .onSuccess { (url, doc) -> saveModuleHeaders(doc, url) }
    }

    private fun saveModuleHeaders(doc: Document, url: HttpUrl) {
        val htmlContent = doc.select("script").html()
        val moduleHeaders = ModuleHeaders(
            token = getScriptParam("antiForgeryToken", htmlContent),
            appGuid = getScriptParam("appGuid", htmlContent),
            appVersion = getScriptParam("version", htmlContent).ifBlank {
                getScriptParam("appVersion", htmlContent)
            },
            email = getScriptParam("name", htmlContent),
            symbol = getScriptParam("appCustomerDb", htmlContent),
        )

        if (moduleHeaders.token.isBlank()) {
            logger.info("There is no token found on $url")
            return
        }

        moduleHeaders.appVersion.substringAfterLast(".").toIntOrNull()?.let {
            ApiEndpoints.currentVersion = it
        }

        when {
            MessagesModuleHost in url.host -> headersByHost[MessagesModuleHost] = moduleHeaders
            StudentPlusModuleHost in url.host -> headersByHost[StudentPlusModuleHost] = moduleHeaders
            StudentModuleHost in url.host -> headersByHost[StudentModuleHost] = moduleHeaders
        }
    }

    private fun Request.attachModuleHeaders(): Request {
        val headers = when {
            MessagesModuleHost in url.host -> headersByHost[MessagesModuleHost]
            StudentPlusModuleHost in url.host -> headersByHost[StudentPlusModuleHost]
            StudentModuleHost in url.host -> headersByHost[StudentModuleHost]
            else -> return this
        }
        logger.info("X-V-AppVersion: ${headers?.appVersion}")

        val mappedUrl = when {
            MessagesModuleHost in url.host -> url.mapModuleUrls(MessagesModuleHost, url, headers?.appVersion)
            StudentPlusModuleHost in url.host -> url.mapModuleUrls(StudentPlusModuleHost, url, headers?.appVersion)
            StudentModuleHost in url.host -> url.mapModuleUrls(StudentModuleHost, url, headers?.appVersion)
            else -> url
        }

        return newBuilder()
            .apply {
                headers?.let {
                    addHeader("X-V-RequestVerificationToken", it.token)
                    addHeader("X-V-AppGuid", it.appGuid)
                    addHeader("X-V-AppVersion", it.appVersion)
                    attachVToken(url, headers)
                }
            }
            .url(mappedUrl)
            .build()
    }

    private fun checkRequest() {
        if (emptyCookieJarIntercept && !cookieJarCabinet.isUserCookiesExist()) {
            throw NotLoggedInException("No cookie found! You are not logged in yet")
        }
    }

    private fun checkResponse(doc: Document, url: String, response: Response) {
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

        // old error style
        val bodyContent = doc.body().text()
        if ("The custom error module" in bodyContent) {
            throw NotLoggedInException(bodyContent)
        }

        // new error style
        val isCodeMatch = response.code == HttpURLConnection.HTTP_OK
        val isJsonContent = bodyContent.startsWith("{")
        val isSubdomainMatch = "uonetplus-uczen" in url
        if (isCodeMatch && isJsonContent && isSubdomainMatch) {
            runCatching { json.decodeFromString<ApiResponse<Unit?>>(bodyContent) }
                .onFailure { logger.error("AutoLoginInterceptor: Can't deserialize new style error content body", it) }
                .onSuccess {
                    it.feedback?.message?.let { errorMessage ->
                        if ("Brak uprawnień" in errorMessage) {
                            throw NotLoggedInException(errorMessage)
                        }
                    }
                }
        }
    }

    private fun checkHttpErrorResponse(error: VulcanClientError, url: String) {
        val isCodeMatch = error.httpCode == HttpURLConnection.HTTP_CONFLICT
        val isSubdomainMatch = MessagesModuleHost in url || StudentPlusModuleHost in url
        if (isCodeMatch && isSubdomainMatch) {
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

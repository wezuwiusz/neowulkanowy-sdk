package io.github.wulkanowy.sdk.scrapper.interceptor

import io.github.wulkanowy.sdk.scrapper.ApiEndpointsResponseMapping
import io.github.wulkanowy.sdk.scrapper.ApiResponse
import io.github.wulkanowy.sdk.scrapper.CookieJarCabinet
import io.github.wulkanowy.sdk.scrapper.Scrapper
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFS
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFSCards
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFSLight
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFSLightCufs
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.ADFSLightScoped
import io.github.wulkanowy.sdk.scrapper.Scrapper.LoginType.STANDARD
import io.github.wulkanowy.sdk.scrapper.exception.VulcanClientError
import io.github.wulkanowy.sdk.scrapper.exception.VulcanServerError
import io.github.wulkanowy.sdk.scrapper.getModuleHeadersFromDocument
import io.github.wulkanowy.sdk.scrapper.getModuleHost
import io.github.wulkanowy.sdk.scrapper.getPathIndexByModuleHost
import io.github.wulkanowy.sdk.scrapper.isAnyMappingAvailable
import io.github.wulkanowy.sdk.scrapper.login.LoginModuleResult
import io.github.wulkanowy.sdk.scrapper.login.LoginResult
import io.github.wulkanowy.sdk.scrapper.login.ModuleHeaders
import io.github.wulkanowy.sdk.scrapper.login.NotLoggedInException
import io.github.wulkanowy.sdk.scrapper.login.UrlGenerator
import io.github.wulkanowy.sdk.scrapper.mapModuleUrl
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository.Companion.SELECTOR_ADFS
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository.Companion.SELECTOR_ADFS_CARDS
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository.Companion.SELECTOR_ADFS_LIGHT
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository.Companion.SELECTOR_STANDARD
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import okio.BufferedSource
import okio.use
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
    private val fetchModuleCookies: (UrlGenerator.Site) -> LoginModuleResult,
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
                performRequest(chain, request)
            } catch (e: Throwable) {
                when (e) {
                    is VulcanClientError -> checkHttpErrorResponse(e, url)
                    is VulcanServerError -> checkServerError(e, url)
                }
                throw e
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
                    performRequest(chain, chain.request())
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

                performRequest(chain, chain.request())
            }
        }
    }

    private fun getModuleCookies(site: UrlGenerator.Site): Result<LoginModuleResult> = runCatching { fetchModuleCookies(site) }
        .onFailure { logger.error("Error in $site login", it) }
        .onSuccess { (url, doc) -> saveModuleHeaders(doc, url) }

    private fun saveModuleHeaders(doc: Document, url: HttpUrl) {
        val moduleHeaders = runBlocking { getModuleHeadersFromDocument(doc) }

        if (moduleHeaders.token.isBlank()) {
            logger.info("There is no token found on $url")
            return
        }

        when {
            MessagesModuleHost in url.host -> headersByHost[MessagesModuleHost] = moduleHeaders
            StudentPlusModuleHost in url.host -> headersByHost[StudentPlusModuleHost] = moduleHeaders
            StudentModuleHost in url.host -> headersByHost[StudentModuleHost] = moduleHeaders
        }
    }

    private fun Request.attachModuleHeaders(): Request {
        val moduleHost = when {
            MessagesModuleHost in url.host -> MessagesModuleHost
            StudentPlusModuleHost in url.host -> StudentPlusModuleHost
            StudentModuleHost in url.host -> StudentModuleHost
            else -> ""
        }

        val headers = headersByHost[moduleHost]
        val mappedUrl = url.mapModuleUrl(moduleHost, headers?.appVersion)

        logger.info("X-V-AppVersion: ${headers?.appVersion}")

        return newBuilder()
            .apply {
                headers?.let {
                    addHeader("X-V-RequestVerificationToken", it.token)
                    addHeader("X-V-AppGuid", it.appGuid)
                    addHeader("X-V-AppVersion", it.appVersion)
                }
            }.url(mappedUrl)
            .build()
    }

    private fun performRequest(chain: Interceptor.Chain, request: Request): Response {
        val response = chain.proceed(request.attachModuleHeaders())
        val url = request.url.toString()
        val uri = request.url

        return if (response.body?.contentType()?.subtype != "json") {
            val body = response.peekBody(Long.MAX_VALUE).byteStream()
            val html = Jsoup.parse(body, null, url)
            checkResponse(html, url, response)
            saveModuleHeaders(html, uri)
            response
        } else {
            handleResponseMapping(response, uri)
        }
    }

    private fun handleResponseMapping(response: Response, uri: HttpUrl): Response {
        val moduleHost = getModuleHost(uri)
        val pathSegmentIndex = getPathIndexByModuleHost(moduleHost)
        val pathKey = uri.pathSegments.getOrNull(pathSegmentIndex)

        val headers = headersByHost[moduleHost]
        val mappings = Scrapper.responseMap[headers?.appVersion]?.get(moduleHost)
            ?: ApiEndpointsResponseMapping[headers?.appVersion]?.get(moduleHost)
        if (mappings.isNullOrEmpty()) return response

        val jsonMappings = mappings[pathKey] ?: mappings["__common__"]

        return response.body?.byteStream()?.bufferedReader()?.use {
            val contentType = response.body?.contentType()
            val body = mapResponseContent(
                response = Json.decodeFromString<JsonElement>(it.readText()),
                jsonMappings = jsonMappings.orEmpty(),
            ).toString().toResponseBody(contentType)
            response.newBuilder().body(body).build()
        } ?: response
    }

    private fun mapResponseContent(response: JsonElement, jsonMappings: Map<String, String>): JsonElement = when (response) {
        is JsonArray -> JsonArray(
            response.jsonArray.map {
                when (it) {
                    is JsonArray -> it.jsonArray
                    is JsonObject -> mapJsonObjectKeys(it.jsonObject, jsonMappings)
                    else -> it
                }
            },
        )

        is JsonObject -> mapJsonObjectKeys(response.jsonObject, jsonMappings)
        else -> response
    }

    private fun mapJsonObjectKeys(jsonObject: JsonObject, jsonMappings: Map<String, String>): JsonObject = JsonObject(
        jsonObject
            .map {
                (jsonMappings[it.key] ?: it.key) to mapResponseContent(it.value, jsonMappings)
            }.toMap(),
    )

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
        val isStudentModuleSubdomain = StudentModuleHost in url
        if (isCodeMatch && isJsonContent && isStudentModuleSubdomain) {
            checkResponseStudentModule(bodyContent)
        }
    }

    private fun checkResponseStudentModule(bodyContent: String) {
        runCatching { json.decodeFromString<ApiResponse<Unit?>>(bodyContent) }
            .onFailure { logger.error("AutoLoginInterceptor: Can't deserialize new style error content body", it) }
            .onSuccess {
                it.feedback?.message?.let { errorMessage ->
                    if ("Brak uprawnień" in errorMessage) {
                        throw NotLoggedInException(errorMessage)
                    }

                    // workaround - access resource before request mapping
                    if ("was not found on controller" in errorMessage && headersByHost[StudentModuleHost] == null) {
                        throw NotLoggedInException(errorMessage)
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

    private fun checkServerError(error: VulcanServerError, url: String) {
        val isCodeMatch = error.httpCode == HttpURLConnection.HTTP_OK
        val isSubdomainMatch = MessagesModuleHost in url || StudentModuleHost in url
        val isMappable = isAnyMappingAvailable(url)

        // workaround - access resource before request mapping
        if (isCodeMatch && isSubdomainMatch && isMappable) {
            throw NotLoggedInException(error.message.orEmpty())
        }
    }

    /**
     * @see [https://github.com/square/retrofit/issues/3110#issuecomment-536248102]
     */
    private fun HttpException.toOkHttpResponse(request: Request) = Response
        .Builder()
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
        ).build()
}

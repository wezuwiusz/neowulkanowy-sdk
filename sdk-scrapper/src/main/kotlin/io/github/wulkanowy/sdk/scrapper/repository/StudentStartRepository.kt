package io.github.wulkanowy.sdk.scrapper.repository

import io.github.wulkanowy.sdk.scrapper.exception.ScrapperException
import io.github.wulkanowy.sdk.scrapper.interceptor.handleErrors
import io.github.wulkanowy.sdk.scrapper.login.CertificateResponse
import io.github.wulkanowy.sdk.scrapper.login.UrlGenerator
import io.github.wulkanowy.sdk.scrapper.register.Semester
import io.github.wulkanowy.sdk.scrapper.register.toSemesters
import io.github.wulkanowy.sdk.scrapper.service.StudentService
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import pl.droidsonroids.jspoon.Jspoon
import java.net.HttpURLConnection

internal class StudentStartRepository(
    private val studentId: Int,
    private val classId: Int,
    private val unitId: Int,
    private val api: StudentService,
    private val urlGenerator: UrlGenerator,
) {

    @Volatile
    private var isCookiesFetched: Boolean = false

    private val cookiesFetchMutex = Mutex()

    private var cachedStart: String = ""

    private val certificateAdapter by lazy {
        Jspoon.create().adapter(CertificateResponse::class.java)
    }

    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    fun clearStartCache() {
        isCookiesFetched = false
        cachedStart = ""
    }

    private suspend fun fetchCookies() {
        if (isCookiesFetched) return

        cookiesFetchMutex.withLock {
            if (isCookiesFetched) return@withLock

            runCatching {
                val start = api.getStart("LoginEndpoint.aspx")
                cachedStart = start

                if ("Working" !in Jsoup.parse(start).title()) {
                    isCookiesFetched = true
                    return@withLock
                }

                val cert = certificateAdapter.fromHtml(start)
                cachedStart = api.sendCertificate(
                    referer = urlGenerator.createReferer(UrlGenerator.Site.STUDENT),
                    url = cert.action,
                    certificate = mapOf(
                        "wa" to cert.wa,
                        "wresult" to cert.wresult,
                        "wctx" to cert.wctx,
                    ),
                )
                isCookiesFetched = true
            }
                .recoverCatching {
                    when {
                        it is ScrapperException && it.code == HttpURLConnection.HTTP_NOT_FOUND -> {
                            cachedStart = api.getStart(urlGenerator.generate(UrlGenerator.Site.STUDENT) + "Start")
                            isCookiesFetched = true
                        }

                        else -> throw it
                    }
                }.getOrThrow()
        }
    }

    suspend fun getSemesters(): List<Semester> {
        fetchCookies()

        val diaries = api.getDiaries().handleErrors().data
        return diaries?.toSemesters(studentId, classId, unitId).orEmpty()
            .sortedByDescending { it.semesterId }
            .ifEmpty {
                logger.debug("Student {}, class {} not found in diaries: {}", studentId, classId, diaries)
                emptyList()
            }
    }
}

package io.github.wulkanowy.sdk.scrapper.repository

import io.github.wulkanowy.sdk.scrapper.interceptor.handleErrors
import io.github.wulkanowy.sdk.scrapper.login.CertificateResponse
import io.github.wulkanowy.sdk.scrapper.login.UrlGenerator
import io.github.wulkanowy.sdk.scrapper.register.RegisterStudent
import io.github.wulkanowy.sdk.scrapper.register.Semester
import io.github.wulkanowy.sdk.scrapper.register.getStudentsFromDiaries
import io.github.wulkanowy.sdk.scrapper.register.toSemesters
import io.github.wulkanowy.sdk.scrapper.service.StudentService
import io.github.wulkanowy.sdk.scrapper.timetable.CacheResponse
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import pl.droidsonroids.jspoon.Jspoon
import java.io.IOException

internal class StudentStartRepository(
    private val studentId: Int,
    private val classId: Int,
    private val unitId: Int,
    private val api: StudentService,
    private val urlGenerator: UrlGenerator,
) {

    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    private val certificateAdapter by lazy {
        Jspoon.create().adapter(CertificateResponse::class.java)
    }

    suspend fun getSemesters(): List<Semester> {
        val diaries = api.getDiaries().handleErrors().data
        return diaries?.toSemesters(studentId, classId, unitId).orEmpty()
            .sortedByDescending { it.semesterId }
            .ifEmpty {
                logger.debug("Student {}, class {} not found in diaries: {}", studentId, classId, diaries)
                emptyList()
            }
    }

    suspend fun getStudent(studentId: Int, unitId: Int): RegisterStudent {
        return api.getDiaries().handleErrors().data.orEmpty()
            .getStudentsFromDiaries(
                isParent = getCache().isParent,
                isEduOne = false,
                unitId = unitId,
            ).find { it.studentId == studentId }
            ?: throw NoSuchElementException()
    }

    private suspend fun getCache(): CacheResponse {
        loginModule()
        return api.getUserCache().handleErrors().let {
            requireNotNull(it.data)
        }
    }

    private suspend fun loginModule() {
        val site = UrlGenerator.Site.STUDENT
        val startHtml = api.getModuleStart()
        val startDoc = Jsoup.parse(startHtml)

        if ("Working" in startDoc.title()) {
            val cert = certificateAdapter.fromHtml(startHtml)
            val certResponseHtml = api.sendModuleCertificate(
                referer = urlGenerator.createReferer(site),
                url = cert.action,
                certificate = mapOf(
                    "wa" to cert.wa,
                    "wresult" to cert.wresult,
                    "wctx" to cert.wctx,
                ),
            )
            val certResponseDoc = Jsoup.parse(certResponseHtml)
            if ("antiForgeryToken" !in certResponseHtml) {
                throw IOException("Unknown module start page: ${certResponseDoc.title()}")
            } else {
                logger.debug("{} cookies fetch successfully!", site)
            }
        } else {
            logger.debug("{} cookies already fetched!", site)
        }
    }
}

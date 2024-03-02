package io.github.wulkanowy.sdk.scrapper.repository

import io.github.wulkanowy.sdk.scrapper.getScriptParam
import io.github.wulkanowy.sdk.scrapper.home.DirectorInformation
import io.github.wulkanowy.sdk.scrapper.home.GovernmentUnit
import io.github.wulkanowy.sdk.scrapper.home.LastAnnouncement
import io.github.wulkanowy.sdk.scrapper.home.LuckyNumber
import io.github.wulkanowy.sdk.scrapper.interceptor.handleErrors
import io.github.wulkanowy.sdk.scrapper.service.HomepageService
import io.github.wulkanowy.sdk.scrapper.toLocalDate
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Instant
import java.time.LocalDate

internal class HomepageRepository(private val api: HomepageService) {

    private val lock = Mutex()

    private var cachedToken: Pair<Instant, String>? = null

    private suspend fun getToken(): String {
        val token = lock.withLock {
            val previousToken = cachedToken?.let {
                when {
                    Instant.now().isBefore(it.first.plusSeconds(5)) -> it.second
                    else -> null
                }
            }
            when {
                previousToken != null -> previousToken
                else -> {
                    val permissions = getScriptParam("permissions", api.getStart())
                    cachedToken = Instant.now() to permissions
                    permissions
                }
            }
        }
        return token
    }

    suspend fun getDirectorInformation(): List<DirectorInformation> {
        val res = api.getDirectorInformation(getToken()).handleErrors().data
        return requireNotNull(res).flatMap { wrapper ->
            wrapper.content.map {
                DirectorInformation(
                    date = it.name.substringBefore(" ").toLocalDate("dd.MM.yyyy"),
                    subject = it.name.substringAfter(" "),
                    content = it.data.orEmpty(),
                )
            }
        }.sortedBy { it.date }
    }

    suspend fun getLastAnnouncements(): List<LastAnnouncement> {
        val res = api.getLastAnnouncements(getToken()).handleErrors().data
        return requireNotNull(res).flatMap { wrapper ->
            wrapper.content.map {
                LastAnnouncement(
                    subject = it.name.trim(),
                    date = it.symbol?.substringBefore(", ")?.toLocalDate("dd.MM.yyyy") ?: LocalDate.EPOCH,
                    author = it.symbol?.substringAfter(", ").orEmpty(),
                    content = it.data.orEmpty(),
                )
            }
        }
    }

    suspend fun getSelfGovernments(): List<GovernmentUnit> {
        val res = api.getSelfGovernments(getToken()).handleErrors().data
        return requireNotNull(res)
    }

    suspend fun getStudentThreats(): List<String> {
        return api.getStudentThreats(getToken()).handleErrors().data.orEmpty()[0].content.map { it.name }
    }

    suspend fun getStudentsTrips(): List<String> {
        return api.getStudentsTrips(getToken()).handleErrors().data.orEmpty()[0].content.map { it.name }
    }

    suspend fun getLastGrades(): List<String> {
        return api.getLastGrades(getToken()).handleErrors().data.orEmpty()[0].content.map { it.name }
    }

    suspend fun getFreeDays(): List<String> {
        return api.getFreeDays(getToken()).handleErrors().data.orEmpty()[0].content.map { it.name }
    }

    suspend fun getKidsLuckyNumbers(): List<LuckyNumber> {
        val res = api.getKidsLuckyNumbers(getToken()).handleErrors().data
        return requireNotNull(res).flatMap { unit ->
            unit.content.flatMap { school ->
                school.content.map { number ->
                    LuckyNumber(
                        unitName = unit.name,
                        school = school.name,
                        number = number.name.substringAfterLast(": ").toInt(),
                    )
                }
            }
        }
    }

    suspend fun getKidsLessonPlan(): List<String> {
        return api.getKidsLessonPlan(getToken()).handleErrors().data.orEmpty()[0].content.map { it.name }
    }

    suspend fun getLastHomework(): List<String> {
        return api.getLastHomework(getToken()).handleErrors().data.orEmpty()[0].content.map { it.name }
    }

    suspend fun getLastTests(): List<String> {
        return api.getLastTests(getToken()).handleErrors().data.orEmpty()[0].content.map { it.name }
    }

    suspend fun getLastStudentLessons(): List<String> {
        return api.getLastStudentLessons(getToken()).handleErrors().data.orEmpty()[0].content.map { it.name }
    }
}

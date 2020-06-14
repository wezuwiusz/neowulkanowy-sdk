package io.github.wulkanowy.sdk.scrapper.repository

import io.github.wulkanowy.sdk.scrapper.getScriptParam
import io.github.wulkanowy.sdk.scrapper.home.GovernmentUnit
import io.github.wulkanowy.sdk.scrapper.home.LuckyNumber
import io.github.wulkanowy.sdk.scrapper.interceptor.handleErrors
import io.github.wulkanowy.sdk.scrapper.service.HomepageService

class HomepageRepository(private val api: HomepageService) {

    private lateinit var token: String

    private suspend fun getToken(): String {
        if (::token.isInitialized) return token

        val page = api.getStart()
        val permissions = getScriptParam("permissions", page)
        token = permissions
        return permissions
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
        return requireNotNull(res).map { unit ->
            unit.content.map { school ->
                school.content.map { number ->
                    LuckyNumber(
                        unitName = unit.name,
                        school = school.name,
                        number = number.name.substringAfterLast(": ").toInt()
                    )
                }
            }.flatten()
        }.flatten()
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

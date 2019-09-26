package io.github.wulkanowy.api.repository

import io.github.wulkanowy.api.getScriptParam
import io.github.wulkanowy.api.home.LuckyNumber
import io.github.wulkanowy.api.interceptor.ErrorHandlerTransformer
import io.github.wulkanowy.api.service.HomepageService
import io.reactivex.Maybe
import io.reactivex.Single

class HomepageRepository(private val api: HomepageService) {

    private lateinit var token: String

    private fun getToken(): Single<String> {
        if (::token.isInitialized) return Single.just(token)

        return api.getStart().map {
            getScriptParam("permissions", it)
        }.map { it.apply { token = this } }
    }

    fun getSelfGovernments(): Single<List<String>> {
        return getToken().flatMap { api.getSelfGovernments(it) }
            .compose(ErrorHandlerTransformer()).map { it.data }.map { it[0].content }.map { res ->
                res.map { it.name }
            }
    }

    fun getStudentsTrips(): Single<List<String>> {
        return getToken().flatMap { api.getStudentsTrips(it) }
            .compose(ErrorHandlerTransformer()).map { it.data }.map { it[0].content }.map { res ->
                res.map { it.name }
            }
    }

    fun getLastGrades(): Single<List<String>> {
        return getToken().flatMap { api.getLastGrades(it) }
            .compose(ErrorHandlerTransformer()).map { it.data }.map { it[0].content }.map { res ->
                res.map { it.name }
            }
    }

    fun getFreeDays(): Single<List<String>> {
        return getToken().flatMap { api.getFreeDays(it) }
            .compose(ErrorHandlerTransformer()).map { it.data }.map { it[0].content }.map { res ->
                res.map { it.name }
            }
    }

    fun getKidsLuckyNumbers(): Single<List<LuckyNumber>> {
        return getToken().flatMap { api.getKidsLuckyNumbers(it) }
            .compose(ErrorHandlerTransformer()).map { it.data }
            .map { it[0].content }
            .map { res ->
                res.map { item ->
                    item.content.map { number ->
                        LuckyNumber(
                            originalContent = number.name,
                            schoolName = item.name,
                            number = number.name.substringAfterLast(": ").toInt()
                        )
                    }
                }.flatten()
            }
    }

    fun getKidsLessonPlan(): Single<List<String>> {
        return getToken().flatMap { api.getKidsLessonPlan(it) }
            .compose(ErrorHandlerTransformer()).map { it.data }.map { it[0].content }
            .map { res -> res.map { it.name } }
    }

    fun getLastHomework(): Single<List<String>> {
        return getToken().flatMap { api.getLastHomework(it) }
            .compose(ErrorHandlerTransformer()).map { it.data }.map { it[0].content }.map { res ->
                res.map { it.name }
            }
    }

    fun getLastTests(): Single<List<String>> {
        return getToken().flatMap { api.getLastTests(it) }
            .compose(ErrorHandlerTransformer()).map { it.data }.map { it[0].content }.map { res ->
                res.map { it.name }
            }
    }

    fun getLastStudentLessons(): Single<List<String>> {
        return getToken().flatMap { api.getLastStudentLessons(it) }
            .compose(ErrorHandlerTransformer()).map { it.data }.map { it[0].content }.map { res ->
                res.map { it.name }
            }
    }

    @Deprecated("Deprecated due to VULCAN homepage update 19.06", ReplaceWith("getKidsLuckyNumbers()"))
    fun getLuckyNumber(): Maybe<Int> {
        return getToken().flatMap { api.getKidsLuckyNumbers(it) }
            .compose(ErrorHandlerTransformer()).map { it.data }
            .filter { it.isNotEmpty() }
            .map { it[0].content }
            .filter { it.isNotEmpty() }
            .map { it[0].content }
            .filter { it.isNotEmpty() }
            .map { it[0].name }
            .filter { it.isNotBlank() }
            .map { it.substringAfterLast(": ").toInt() }
    }
}

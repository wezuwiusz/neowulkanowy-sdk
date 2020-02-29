package io.github.wulkanowy.sdk.scrapper.repository

import io.github.wulkanowy.sdk.scrapper.getScriptParam
import io.github.wulkanowy.sdk.scrapper.home.LuckyNumber
import io.github.wulkanowy.sdk.scrapper.interceptor.ErrorHandlerTransformer
import io.github.wulkanowy.sdk.scrapper.service.HomepageService
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
            .compose(ErrorHandlerTransformer()).map { it.data.orEmpty() }.map { it[0].content }.map { res ->
                res.map { it.name }
            }
    }

    fun getStudentsTrips(): Single<List<String>> {
        return getToken().flatMap { api.getStudentsTrips(it) }
            .compose(ErrorHandlerTransformer()).map { it.data.orEmpty() }.map { it[0].content }.map { res ->
                res.map { it.name }
            }
    }

    fun getLastGrades(): Single<List<String>> {
        return getToken().flatMap { api.getLastGrades(it) }
            .compose(ErrorHandlerTransformer()).map { it.data.orEmpty() }.map { it[0].content }.map { res ->
                res.map { it.name }
            }
    }

    fun getFreeDays(): Single<List<String>> {
        return getToken().flatMap { api.getFreeDays(it) }
            .compose(ErrorHandlerTransformer()).map { it.data.orEmpty() }.map { it[0].content }.map { res ->
                res.map { it.name }
            }
    }

    fun getKidsLuckyNumbers(): Single<List<LuckyNumber>> {
        return getToken().flatMap { api.getKidsLuckyNumbers(it) }
            .compose(ErrorHandlerTransformer()).map { it.data.orEmpty() }
            .map { res ->
                res.map { unit ->
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
    }

    fun getKidsLessonPlan(): Single<List<String>> {
        return getToken().flatMap { api.getKidsLessonPlan(it) }
            .compose(ErrorHandlerTransformer()).map { it.data.orEmpty() }.map { it[0].content }
            .map { res -> res.map { it.name } }
    }

    fun getLastHomework(): Single<List<String>> {
        return getToken().flatMap { api.getLastHomework(it) }
            .compose(ErrorHandlerTransformer()).map { it.data.orEmpty() }.map { it[0].content }.map { res ->
                res.map { it.name }
            }
    }

    fun getLastTests(): Single<List<String>> {
        return getToken().flatMap { api.getLastTests(it) }
            .compose(ErrorHandlerTransformer()).map { it.data.orEmpty() }.map { it[0].content }.map { res ->
                res.map { it.name }
            }
    }

    fun getLastStudentLessons(): Single<List<String>> {
        return getToken().flatMap { api.getLastStudentLessons(it) }
            .compose(ErrorHandlerTransformer()).map { it.data.orEmpty() }.map { it[0].content }.map { res ->
                res.map { it.name }
            }
    }
}

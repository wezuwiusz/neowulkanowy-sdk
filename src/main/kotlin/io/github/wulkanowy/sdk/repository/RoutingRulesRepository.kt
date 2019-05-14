package io.github.wulkanowy.sdk.repository

import io.github.wulkanowy.sdk.service.RoutingRulesService
import io.reactivex.Single

class RoutingRulesRepository(private val api: RoutingRulesService) {

    fun getRouteByToken(token: String): Single<String> {
        val tokenSymbol = token.substring(0..2)
        if ("FK1" == tokenSymbol) return Single.just("https://api.fakelog.cf")

        return api.getRoutingRules().map { routes ->
            routes.split("\r?\n".toRegex())
                    .singleOrNull { tokenSymbol == it.substringBefore(",") }?.substringAfter(",")
        }
    }
}

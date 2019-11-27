package io.github.wulkanowy.sdk.mobile.repository

import io.github.wulkanowy.sdk.mobile.exception.InvalidTokenException
import io.github.wulkanowy.sdk.mobile.service.RoutingRulesService
import io.reactivex.Single

class RoutingRulesRepository(private val api: RoutingRulesService) {

    fun getRouteByToken(token: String): Single<String> {
        if (token.length < 4) return Single.error<String>(InvalidTokenException("Token is too short"))

        val tokenSymbol = token.substring(0..2)

        if ("FK1" == tokenSymbol) return Single.just("https://api.fakelog.cf")

        return api.getRoutingRules().map { routes ->
            routes.split("\r?\n".toRegex())
                .singleOrNull { tokenSymbol == it.substringBefore(",") }
                ?.substringAfter(",")
                ?: throw InvalidTokenException("This token is unsupported")
        }
    }
}

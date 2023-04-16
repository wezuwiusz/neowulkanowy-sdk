package io.github.wulkanowy.sdk.hebe.repository

import io.github.wulkanowy.sdk.hebe.exception.InvalidTokenException
import io.github.wulkanowy.sdk.hebe.exception.UnknownTokenException
import io.github.wulkanowy.sdk.hebe.service.RoutingRulesService

internal class RoutingRulesRepository(private val api: RoutingRulesService) {

    suspend fun getRouteByToken(token: String): String {
        if (token.length < 4) throw InvalidTokenException("Token '$token' is too short")

        val tokenSymbol = token.substring(0..2)

        if ("FK1" == tokenSymbol) return "https://api.fakelog.cf"

        return api.getRoutingRules()
            .split("\r?\n".toRegex())
            .singleOrNull { tokenSymbol == it.substringBefore(",") }
            ?.substringAfter(",")
            ?: throw UnknownTokenException("This token: '$token' is unsupported")
    }
}

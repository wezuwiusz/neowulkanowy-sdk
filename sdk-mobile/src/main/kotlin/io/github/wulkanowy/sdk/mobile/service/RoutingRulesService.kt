package io.github.wulkanowy.sdk.mobile.service

import retrofit2.http.GET

interface RoutingRulesService {

    @GET("/UonetPlusMobile/RoutingRules.txt")
    suspend fun getRoutingRules(): String
}

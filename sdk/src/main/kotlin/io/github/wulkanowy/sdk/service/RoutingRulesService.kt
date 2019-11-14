package io.github.wulkanowy.sdk.service

import io.reactivex.Single
import retrofit2.http.GET

interface RoutingRulesService {

    @GET("/UonetPlusMobile/RoutingRules.txt")
    fun getRoutingRules(): Single<String>
}

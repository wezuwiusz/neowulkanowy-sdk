package io.github.wulkanowy.sdk.prometheus.service

import retrofit2.http.GET

interface InfoService {
    @GET("/konto")
    suspend fun getAccountInfo(): String
}

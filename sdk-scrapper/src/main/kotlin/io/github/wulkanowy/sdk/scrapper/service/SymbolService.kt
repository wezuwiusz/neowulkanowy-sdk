package io.github.wulkanowy.sdk.scrapper.service

import retrofit2.http.GET
import retrofit2.http.Path

internal interface SymbolService {

    @GET("/{symbol}/")
    suspend fun getSymbolPage(@Path("symbol") symbol: String)
}

package io.github.wulkanowy.api.service

import io.github.wulkanowy.api.register.HomepageResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface HomepageService {

    @GET("{symbol}/Start.mvc/Index")
    fun getStartInfo(@Path("symbol") symbol: String): Single<HomepageResponse>
}

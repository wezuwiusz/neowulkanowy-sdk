package io.github.wulkanowy.api.interfaces

import io.github.wulkanowy.api.register.HomepageResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface HomepageApi {

    @GET("{symbol}/Start.mvc/Index")
    fun getStartInfo(@Path("symbol") symbol: String): Single<HomepageResponse>
}

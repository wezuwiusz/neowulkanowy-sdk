package io.github.wulkanowy.sdk.scrapper.service

import io.github.wulkanowy.sdk.scrapper.register.LoginForm
import retrofit2.http.GET
import retrofit2.http.Url

interface RegisterService {

    @GET
    suspend fun getFormType(@Url url: String): LoginForm
}

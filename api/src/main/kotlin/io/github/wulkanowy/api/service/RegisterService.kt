package io.github.wulkanowy.api.service

import io.github.wulkanowy.api.register.LoginForm
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Url

interface RegisterService {

    @GET
    fun getFormType(@Url url: String): Single<LoginForm>
}

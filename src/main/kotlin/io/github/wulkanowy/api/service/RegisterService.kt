package io.github.wulkanowy.api.service

import io.github.wulkanowy.api.register.LoginForm
import io.reactivex.Single
import retrofit2.http.GET

interface RegisterService {

    @GET(".")
    fun getFormType(): Single<LoginForm>
}

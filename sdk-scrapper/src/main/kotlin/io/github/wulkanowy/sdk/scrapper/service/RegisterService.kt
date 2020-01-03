package io.github.wulkanowy.sdk.scrapper.service

import io.github.wulkanowy.sdk.scrapper.register.LoginForm
import io.github.wulkanowy.sdk.scrapper.register.UnlockAccount
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Url

interface RegisterService {

    @GET
    fun getFormType(@Url url: String): Single<LoginForm>

    @GET
    fun getPasswordResetPageWithCaptcha(@Url url: String): Single<UnlockAccount>
}

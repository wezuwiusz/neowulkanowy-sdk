package io.github.wulkanowy.sdk.scrapper.service

import io.github.wulkanowy.sdk.scrapper.register.LoginForm
import io.github.wulkanowy.sdk.scrapper.register.SentUnlockAccountResponse
import io.github.wulkanowy.sdk.scrapper.register.UnlockAccountResponse
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface AccountService {

    @GET
    fun getFormType(@Url url: String): Single<LoginForm>

    @GET
    fun getPasswordResetPageWithCaptcha(@Url url: String): Single<UnlockAccountResponse>

    @POST
    @FormUrlEncoded
    fun sendPasswordResetRequest(
        @Url registerBaseUrl: String,
        @Field("Email") email: String,
        @Field("g-recaptcha-response") captchaCode: String
    ): Single<SentUnlockAccountResponse>
}

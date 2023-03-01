package io.github.wulkanowy.sdk.scrapper.service

import io.github.wulkanowy.sdk.scrapper.register.LoginForm
import io.github.wulkanowy.sdk.scrapper.register.SentUnlockAccountResponse
import io.github.wulkanowy.sdk.scrapper.register.UnlockAccountResponse
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface AccountService {

    @GET
    suspend fun getFormType(@Url url: String): LoginForm

    @GET
    suspend fun getPasswordResetPageWithCaptcha(@Url url: String): UnlockAccountResponse

    @POST
    @FormUrlEncoded
    suspend fun sendPasswordResetRequest(
        @Url url: String,
        @Field("Email") email: String,
        @Field("g-recaptcha-response") captchaCode: String,
    ): SentUnlockAccountResponse

    @POST
    @FormUrlEncoded
    suspend fun sendPasswordResetRequestADFSLight(
        @Url url: String,
        @Field("UserId") username: String,
        @Field("g-recaptcha-response") captchaCode: String,
    ): SentUnlockAccountResponse

    @GET
    suspend fun getPasswordResetPageADFS(@Url url: String): SentUnlockAccountResponse

    @POST
    @FormUrlEncoded
    suspend fun sendPasswordResetRequestADFS(
        @Url url: String,
        @Field("txtUserID") username: String,
        @Field("g-recaptcha-response") captchaCode: String,
        @FieldMap viewStateParams: Map<String, String>,
    ): SentUnlockAccountResponse
}

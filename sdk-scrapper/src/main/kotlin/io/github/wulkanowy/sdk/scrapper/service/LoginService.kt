package io.github.wulkanowy.sdk.scrapper.service

import io.github.wulkanowy.sdk.scrapper.login.ADFSFormResponse
import io.github.wulkanowy.sdk.scrapper.register.HomePageResponse
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface LoginService {

    @POST("Account/LogOn")
    @FormUrlEncoded
    suspend fun sendCredentials(@Query("ReturnUrl") returnUrl: String, @FieldMap credentials: Map<String, String>): String

    @POST
    @FormUrlEncoded
    suspend fun sendCertificate(@Url url: String, @FieldMap certificate: Map<String, String>): HomePageResponse

    @GET
    suspend fun switchLogin(@Url url: String): HomePageResponse

    // ADFS

    @GET
    suspend fun getForm(@Url url: String): ADFSFormResponse

    @POST
    @FormUrlEncoded
    suspend fun sendADFSForm(@Url url: String, @FieldMap values: Map<String, String>): String

    @POST
    @FormUrlEncoded
    suspend fun sendADFSMSForm(@Url url: String, @FieldMap values: Map<String, String>): String
}

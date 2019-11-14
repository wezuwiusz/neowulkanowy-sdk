package io.github.wulkanowy.api.service

import io.github.wulkanowy.api.login.ADFSFormResponse
import io.github.wulkanowy.api.register.SendCertificateResponse
import io.reactivex.Single
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface LoginService {

    @POST("Account/LogOn")
    @FormUrlEncoded
    fun sendCredentials(@Query("ReturnUrl") returnUrl: String, @FieldMap credentials: Map<String, String>): Single<String>

    @POST
    @FormUrlEncoded
    fun sendCertificate(@Url url: String, @FieldMap certificate: Map<String, String>): Single<SendCertificateResponse>

    @GET
    fun switchLogin(@Url url: String): Single<SendCertificateResponse>

    // ADFS

    @GET
    fun getForm(@Url url: String): Single<ADFSFormResponse>

    @POST
    @FormUrlEncoded
    fun sendADFSFormStandardChoice(@Url url: String, @FieldMap formState: Map<String, String>): Single<ADFSFormResponse>

    @POST
    @FormUrlEncoded
    fun sendADFSForm(@Url url: String, @FieldMap values: Map<String, String>): Single<String>
}

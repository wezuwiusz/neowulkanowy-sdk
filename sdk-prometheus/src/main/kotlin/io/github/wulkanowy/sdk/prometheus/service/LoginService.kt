package io.github.wulkanowy.sdk.prometheus.service

import io.github.wulkanowy.sdk.prometheus.ApiResponse
import io.github.wulkanowy.sdk.prometheus.login.QueryUserInfoResponse
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface LoginService {
    @GET("/logowanie")
    suspend fun getLoginPage(): String

    @FormUrlEncoded
    @POST("/Account/QueryUserInfo")
    suspend fun queryUserInfo(@FieldMap fields: Map<String, String>): ApiResponse<QueryUserInfoResponse>

    @FormUrlEncoded
    @POST("/logowanie")
    suspend fun logIn(@FieldMap fields: Map<String, String>): String
}

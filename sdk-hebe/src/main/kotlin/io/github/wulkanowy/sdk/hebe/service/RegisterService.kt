package io.github.wulkanowy.sdk.hebe.service

import io.github.wulkanowy.sdk.hebe.ApiRequest
import io.github.wulkanowy.sdk.hebe.ApiResponse
import io.github.wulkanowy.sdk.hebe.register.RegisterRequest
import io.github.wulkanowy.sdk.hebe.register.RegisterResponse
import io.github.wulkanowy.sdk.hebe.register.StudentInfo
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.POST

interface RegisterService {

    @POST("new")
    fun registerDevice(request: ApiRequest<RegisterRequest>): Single<ApiResponse<RegisterResponse>>

    @GET("hebe")
    fun getStudentsInfo(): Single<ApiResponse<List<StudentInfo>>>
}

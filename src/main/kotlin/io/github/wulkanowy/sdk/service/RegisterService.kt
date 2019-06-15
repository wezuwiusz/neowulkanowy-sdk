package io.github.wulkanowy.sdk.service

import io.github.wulkanowy.sdk.ApiRequest
import io.github.wulkanowy.sdk.ApiResponse
import io.github.wulkanowy.sdk.register.CertificateRequest
import io.github.wulkanowy.sdk.register.CertificateResponse
import io.github.wulkanowy.sdk.register.Student
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface RegisterService {

    @POST("Certyfikat")
    @Headers("RequestMobileType: RegisterDevice")
    fun getCertificate(@Body certificateRequest: CertificateRequest): Single<CertificateResponse>

    @POST("ListaUczniow")
    fun getPupils(@Body pupilsListRequest: ApiRequest): Single<ApiResponse<List<Student>>>
}

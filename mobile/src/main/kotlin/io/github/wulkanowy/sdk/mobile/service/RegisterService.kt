package io.github.wulkanowy.sdk.mobile.service

import io.github.wulkanowy.sdk.mobile.ApiRequest
import io.github.wulkanowy.sdk.mobile.ApiResponse
import io.github.wulkanowy.sdk.mobile.register.CertificateRequest
import io.github.wulkanowy.sdk.mobile.register.CertificateResponse
import io.github.wulkanowy.sdk.mobile.register.Student
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

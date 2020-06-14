package io.github.wulkanowy.sdk.mobile.service

import io.github.wulkanowy.sdk.mobile.ApiRequest
import io.github.wulkanowy.sdk.mobile.ApiResponse
import io.github.wulkanowy.sdk.mobile.register.CertificateRequest
import io.github.wulkanowy.sdk.mobile.register.CertificateResponse
import io.github.wulkanowy.sdk.mobile.register.Student
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface RegisterService {

    @POST("Certyfikat")
    @Headers("RequestMobileType: RegisterDevice")
    suspend fun getCertificate(@Body certificateRequest: CertificateRequest): CertificateResponse

    @POST("ListaUczniow")
    suspend fun getPupils(@Body pupilsListRequest: ApiRequest): ApiResponse<List<Student>>
}

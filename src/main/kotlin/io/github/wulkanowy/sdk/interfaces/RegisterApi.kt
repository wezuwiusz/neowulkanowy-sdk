package io.github.wulkanowy.sdk.interfaces

import io.github.wulkanowy.sdk.base.ApiRequest
import io.github.wulkanowy.sdk.base.ApiResponse
import io.github.wulkanowy.sdk.register.CertificateRequest
import io.github.wulkanowy.sdk.register.CertificateResponse
import io.github.wulkanowy.sdk.register.Student
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface RegisterApi {

    @POST("Certyfikat")
    @Headers("RequestMobileType: RegisterDevice")
    fun getCertificate(@Body certificateRequest: CertificateRequest): Observable<CertificateResponse>

    @POST("ListaUczniow")
    fun getPupils(@Body pupilsListRequest: ApiRequest): Observable<ApiResponse<List<Student>>>
}

package io.github.wulkanowy.sdk.service

import io.github.wulkanowy.sdk.base.ApiRequest
import io.github.wulkanowy.sdk.base.ApiResponse
import io.github.wulkanowy.sdk.register.CertificateRequest
import io.github.wulkanowy.sdk.register.CertificateResponse
import io.github.wulkanowy.sdk.register.Student
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface RegisterService {

    @GET("http://komponenty.vulcan.net.pl/UonetPlusMobile/RoutingRules.txt")
    fun getRoutingRules(): Single<String>

    @POST("Certyfikat")
    @Headers("RequestMobileType: RegisterDevice")
    fun getCertificate(@Body certificateRequest: CertificateRequest): Single<CertificateResponse>

    @POST("ListaUczniow")
    fun getPupils(@Body pupilsListRequest: ApiRequest): Single<ApiResponse<List<Student>>>
}

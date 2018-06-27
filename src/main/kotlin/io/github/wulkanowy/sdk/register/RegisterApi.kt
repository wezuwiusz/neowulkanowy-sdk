package io.github.wulkanowy.sdk.register

import io.github.wulkanowy.sdk.BaseRequest
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import rx.Observable

interface RegisterApi {

    @POST("Certyfikat")
    @Headers("RequestMobileType: RegisterDevice")
    fun getCertificate(@Body certificateRequest: CertificateRequest): Observable<CertificateResponse>

    @POST("ListaUczniow")
    fun getPupils(@Body pupilsListRequest: BaseRequest): Observable<StudentsResponse>
}

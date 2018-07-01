package io.github.wulkanowy.sdk.interfaces

import io.github.wulkanowy.sdk.base.BaseRequest
import io.github.wulkanowy.sdk.register.CertificateRequest
import io.github.wulkanowy.sdk.register.CertificateResponse
import io.github.wulkanowy.sdk.register.StudentsResponse
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

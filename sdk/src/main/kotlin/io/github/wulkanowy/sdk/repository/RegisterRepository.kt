package io.github.wulkanowy.sdk.repository

import io.github.wulkanowy.sdk.ApiRequest
import io.github.wulkanowy.sdk.register.CertificateRequest
import io.github.wulkanowy.sdk.register.CertificateResponse
import io.github.wulkanowy.sdk.register.Student
import io.github.wulkanowy.sdk.service.RegisterService
import io.reactivex.Single

class RegisterRepository(private val api: RegisterService) {

    fun getCertificate(token: String, pin: String, deviceName: String): Single<CertificateResponse> {
        return api.getCertificate(CertificateRequest(tokenKey = token, pin = pin, deviceName = deviceName))
    }

    fun getPupils(): Single<List<Student>> = api.getPupils(object : ApiRequest() {}).map { it.data }
}

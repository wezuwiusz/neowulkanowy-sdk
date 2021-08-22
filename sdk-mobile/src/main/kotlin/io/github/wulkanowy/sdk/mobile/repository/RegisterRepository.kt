package io.github.wulkanowy.sdk.mobile.repository

import io.github.wulkanowy.sdk.mobile.ApiRequest
import io.github.wulkanowy.sdk.mobile.register.CertificateRequest
import io.github.wulkanowy.sdk.mobile.register.CertificateResponse
import io.github.wulkanowy.sdk.mobile.register.Student
import io.github.wulkanowy.sdk.mobile.service.RegisterService

class RegisterRepository(private val api: RegisterService) {

    suspend fun getCertificate(token: String, pin: String, deviceName: String, android: String, firebaseToken: String): CertificateResponse {
        val request = CertificateRequest(
            tokenKey = token,
            pin = pin,
            deviceName = "$deviceName (Wulkanowy)",
            deviceSystemVersion = android,
            firebaseToken = firebaseToken
        )
        return api.getCertificate(request)
    }

    suspend fun getStudents(): List<Student> = api.getPupils(object : ApiRequest() {}).data.orEmpty()
}

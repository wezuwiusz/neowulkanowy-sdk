package io.github.wulkanowy.sdk.hebe.repository

import io.github.wulkanowy.sdk.hebe.ApiRequest
import io.github.wulkanowy.sdk.hebe.ApiResponse
import io.github.wulkanowy.sdk.hebe.register.RegisterDevice
import io.github.wulkanowy.sdk.hebe.register.RegisterRequest
import io.github.wulkanowy.sdk.hebe.register.RegisterResponse
import io.github.wulkanowy.sdk.hebe.register.StudentInfo
import io.github.wulkanowy.sdk.hebe.service.RegisterService

internal class RegisterRepository(private val service: RegisterService) {

    suspend fun register(
        firebaseToken: String,
        token: String,
        pin: String,
        deviceModel: String,
        privatePem: String,
        keyId: String,
    ): RegisterDevice {
        val response = registerDevice(
            privateKey = privatePem,
            certificateId = keyId,
            deviceModel = deviceModel,
            firebaseToken = firebaseToken,
            pin = pin,
            token = token,
        )
        if (response.envelope == null) {
            when (response.status.code) {
                // todo: add more codes
                else -> error("Unknown error: ${response.status.message}")
            }
        }

        val envelope = response.envelope!!
        return RegisterDevice(
            loginId = envelope.loginId,
            restUrl = envelope.restUrl,
            userLogin = envelope.userLogin,
            userName = envelope.userName,
            certificateHash = keyId,
            privatePem = privatePem,
        )
    }

    private suspend fun registerDevice(
        privateKey: String,
        certificateId: String,
        deviceModel: String,
        firebaseToken: String,
        pin: String,
        token: String,
    ): ApiResponse<RegisterResponse> = service.registerDevice(
        ApiRequest(
            certificateId = certificateId,
            firebaseToken = firebaseToken,
            envelope = RegisterRequest(
                certificate = privateKey,
                certificateThumbprint = certificateId,
                deviceModel = deviceModel,
                pin = pin,
                securityToken = token,
            ),
        ),
    )

    suspend fun getStudentInfo(): List<StudentInfo> {
        return service.getStudentsInfo().envelope!!
    }
}

package io.github.wulkanowy.sdk.hebe.repository

import io.github.wulkanowy.sdk.hebe.ApiRequest
import io.github.wulkanowy.sdk.hebe.ApiResponse
import io.github.wulkanowy.sdk.hebe.getEnvelopeOrThrowError
import io.github.wulkanowy.sdk.hebe.register.RegisterRequest
import io.github.wulkanowy.sdk.hebe.register.RegisterResponse
import io.github.wulkanowy.sdk.hebe.register.StudentInfo
import io.github.wulkanowy.sdk.hebe.service.RegisterService

internal class RegisterRepository(private val service: RegisterService) {

    suspend fun register(
        token: String,
        pin: String,
        deviceModel: String,
        certificatePem: String,
        certificateId: String,
        firebaseToken: String?,
    ): RegisterResponse = registerDevice(
        privateKey = certificatePem,
        certificateId = certificateId,
        deviceModel = deviceModel,
        firebaseToken = firebaseToken.orEmpty(),
        pin = pin,
        token = token,
    ).getEnvelopeOrThrowError()!!

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

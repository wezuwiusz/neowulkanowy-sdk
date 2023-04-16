package io.github.wulkanowy.sdk.hebe.repository

import io.github.wulkanowy.sdk.hebe.ApiRequest
import io.github.wulkanowy.sdk.hebe.register.RegisterRequest
import io.github.wulkanowy.sdk.hebe.register.RegisterResponse
import io.github.wulkanowy.sdk.hebe.register.StudentInfo
import io.github.wulkanowy.sdk.hebe.service.RegisterService

internal class RegisterRepository(private val service: RegisterService) {

    suspend fun registerDevice(
        privateKey: String,
        certificateId: String,
        deviceModel: String,
        firebaseToken: String,
        pin: String,
        token: String,
    ): RegisterResponse = service.registerDevice(
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
    ).envelope!!

    suspend fun getStudentInfo(): List<StudentInfo> {
        return service.getStudentsInfo().envelope!!
    }
}

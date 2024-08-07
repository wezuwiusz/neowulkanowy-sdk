package io.github.wulkanowy.sdk.hebe.repository

import io.github.wulkanowy.sdk.hebe.ApiRequest
import io.github.wulkanowy.sdk.hebe.ApiResponse
import io.github.wulkanowy.sdk.hebe.exception.InvalidSymbolException
import io.github.wulkanowy.sdk.hebe.getEnvelopeOrThrowError
import io.github.wulkanowy.sdk.hebe.register.RegisterRequest
import io.github.wulkanowy.sdk.hebe.register.RegisterResponse
import io.github.wulkanowy.sdk.hebe.register.StudentInfo
import io.github.wulkanowy.sdk.hebe.service.RegisterService
import retrofit2.HttpException
import java.net.HttpURLConnection.HTTP_INTERNAL_ERROR

internal class RegisterRepository(
    private val service: RegisterService,
) {

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
    ): ApiResponse<RegisterResponse> = runCatching {
        service.registerDevice(
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
    }.onFailure {
        if (it is HttpException && it.code() == HTTP_INTERNAL_ERROR) {
            if ("ArgumentException" in it
                    .response()
                    ?.errorBody()
                    ?.string()
                    .orEmpty()
            ) {
                throw InvalidSymbolException()
            }
        }
    }.getOrThrow()

    suspend fun getStudentInfo(): List<StudentInfo> = service.getStudentsInfo().envelope!!
}

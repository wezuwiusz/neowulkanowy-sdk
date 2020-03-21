package io.github.wulkanowy.sdk.hebe.service

import io.github.wulkanowy.sdk.hebe.ApiRequest
import io.github.wulkanowy.sdk.hebe.ApiResponse
import io.github.wulkanowy.sdk.hebe.register.RegisterRequest
import io.github.wulkanowy.sdk.hebe.register.RegisterResponse
import io.reactivex.Single

interface RegisterService {

    fun registerDevice(request: ApiRequest<RegisterRequest>): Single<ApiResponse<RegisterResponse>>
}

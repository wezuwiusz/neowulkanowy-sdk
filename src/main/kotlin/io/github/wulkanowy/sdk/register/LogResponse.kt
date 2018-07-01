package io.github.wulkanowy.sdk.register

import com.google.gson.annotations.SerializedName
import io.github.wulkanowy.sdk.base.BaseResponse

data class LogResponse(

    @SerializedName("Data")
    var data: String

) : BaseResponse()

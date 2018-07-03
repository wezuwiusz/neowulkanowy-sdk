package io.github.wulkanowy.sdk.grades

import com.google.gson.annotations.SerializedName
import io.github.wulkanowy.sdk.base.BaseResponse

data class GradesResponse(

    @SerializedName("Data")
    val data: List<Grade>
) : BaseResponse()

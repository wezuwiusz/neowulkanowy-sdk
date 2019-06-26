package io.github.wulkanowy.sdk.school

import com.google.gson.annotations.SerializedName
import io.github.wulkanowy.sdk.ApiRequest

class TeachersRequest(

    @SerializedName("IdUczen")
    val studentId: Int,

    @SerializedName("IdOkresKlasyfikacyjny")
    val semesterId: Int
) : ApiRequest()

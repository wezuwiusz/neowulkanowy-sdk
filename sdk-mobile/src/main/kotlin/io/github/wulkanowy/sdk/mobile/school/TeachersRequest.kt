package io.github.wulkanowy.sdk.mobile.school

import com.google.gson.annotations.SerializedName
import io.github.wulkanowy.sdk.mobile.ApiRequest

class TeachersRequest(

    @SerializedName("IdUczen")
    val studentId: Int,

    @SerializedName("IdOkresKlasyfikacyjny")
    val semesterId: Int
) : ApiRequest()

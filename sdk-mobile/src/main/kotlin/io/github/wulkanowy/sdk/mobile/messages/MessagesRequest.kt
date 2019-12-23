package io.github.wulkanowy.sdk.mobile.messages

import com.google.gson.annotations.SerializedName
import io.github.wulkanowy.sdk.mobile.ApiRequest

data class MessagesRequest(

    @SerializedName("DataPoczatkowa")
    val startDate: String,

    @SerializedName("DataKoncowa")
    val endDate: String,

    @SerializedName("LoginId")
    val loginId: Int,

    @SerializedName("IdUczen")
    val studentId: Int
) : ApiRequest()

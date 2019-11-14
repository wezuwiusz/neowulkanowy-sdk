package io.github.wulkanowy.sdk.messages

import com.google.gson.annotations.SerializedName
import io.github.wulkanowy.sdk.ApiRequest

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

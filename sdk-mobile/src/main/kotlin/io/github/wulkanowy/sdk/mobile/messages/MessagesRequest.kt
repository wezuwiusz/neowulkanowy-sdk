package io.github.wulkanowy.sdk.mobile.messages

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.github.wulkanowy.sdk.mobile.ApiRequest

@JsonClass(generateAdapter = true)
data class MessagesRequest(

    @Json(name = "DataPoczatkowa")
    val startDate: String,

    @Json(name = "DataKoncowa")
    val endDate: String,

    @Json(name = "LoginId")
    val loginId: Int,

    @Json(name = "IdUczen")
    val studentId: Int
) : ApiRequest()

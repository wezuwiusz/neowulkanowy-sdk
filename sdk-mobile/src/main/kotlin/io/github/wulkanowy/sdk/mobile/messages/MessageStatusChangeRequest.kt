package io.github.wulkanowy.sdk.mobile.messages

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.github.wulkanowy.sdk.mobile.ApiRequest

@JsonClass(generateAdapter = true)
data class MessageStatusChangeRequest(

    @Json(name = "WiadomoscId")
    val messageId: Int,

    @Json(name = "FolderWiadomosci")
    val folder: String,

    @Json(name = "Status")
    val status: String,

    @Json(name = "LoginId")
    val loginId: Int,

    @Json(name = "IdUczen")
    val studentId: Int
) : ApiRequest()

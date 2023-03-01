package io.github.wulkanowy.sdk.mobile.messages

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Recipient(

    @Json(name = "LoginId")
    val loginId: Int,

    @Json(name = "Nazwa")
    val name: String,
)

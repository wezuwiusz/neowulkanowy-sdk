package io.github.wulkanowy.sdk.scrapper.mobile

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TokenResponse(

    @Json(name = "TokenKey")
    val token: String,

    @Json(name = "CustomerGroup")
    val symbol: String,

    @Json(name = "PIN")
    val pin: String,

    @Json(name = "QrCodeImage")
    val qrCodeImage: String
)

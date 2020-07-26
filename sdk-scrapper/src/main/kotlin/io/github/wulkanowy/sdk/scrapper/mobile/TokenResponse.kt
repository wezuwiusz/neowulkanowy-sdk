package io.github.wulkanowy.sdk.scrapper.mobile

import com.google.gson.annotations.SerializedName

data class TokenResponse(

    @SerializedName("TokenKey")
    val token: String,

    @SerializedName("CustomerGroup")
    val symbol: String,

    @SerializedName("PIN")
    val pin: String,

    @SerializedName("QrCodeImage")
    val qrCodeImage: String
)

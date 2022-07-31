package io.github.wulkanowy.sdk.scrapper.mobile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(

    @SerialName("TokenKey")
    val token: String,

    @SerialName("CustomerGroup")
    val symbol: String,

    @SerialName("PIN")
    val pin: String,

    @SerialName("QrCodeImage")
    val qrCodeImage: String
)

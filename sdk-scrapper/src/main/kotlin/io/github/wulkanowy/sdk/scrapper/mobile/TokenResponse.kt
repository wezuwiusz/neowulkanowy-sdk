package io.github.wulkanowy.sdk.scrapper.mobile

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class TokenResponse(

    @JsonNames("key")
    @SerialName("TokenKey")
    val token: String,

    @JsonNames("symbolGrupujacy")
    @SerialName("CustomerGroup")
    val symbol: String,

    @JsonNames("pin")
    @SerialName("PIN")
    val pin: String,

    @JsonNames("qrCode")
    @SerialName("QrCodeImage")
    val qrCodeImage: String,
)

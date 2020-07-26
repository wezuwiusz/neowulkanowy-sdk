package io.github.wulkanowy.sdk.scrapper.mobile

import com.google.gson.annotations.SerializedName

class TokenResponse {

    @SerializedName("TokenKey")
    lateinit var token: String

    @SerializedName("CustomerGroup")
    lateinit var symbol: String

    @SerializedName("PIN")
    lateinit var pin: String

    @SerializedName("QrCodeImage")
    lateinit var qrCodeImage: String
}

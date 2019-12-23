package io.github.wulkanowy.sdk.scrapper.mobile

import com.google.gson.annotations.SerializedName
import pl.droidsonroids.jspoon.annotation.Selector

class TokenResponse {

    @SerializedName("TokenKey")
    @Selector("#rejestracja-formularz .blockElement:nth-last-child(3)", regex = ": (.*)")
    lateinit var token: String

    @SerializedName("CustomerGroup")
    @Selector("#rejestracja-formularz .blockElement:nth-last-child(2)", regex = ": (.*)")
    lateinit var symbol: String

    @SerializedName("PIN")
    @Selector("#rejestracja-formularz .blockElement:nth-last-child(1)", regex = ": (.*)")
    lateinit var pin: String

    @SerializedName("QrCodeImage")
    @Selector("#rejestracja-formularz img", attr = "src", regex = "data:image\\/png;base64,(.+)")
    lateinit var qrCodeImage: String
}

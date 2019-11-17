package io.github.wulkanowy.sdk.mobile.register

import com.google.gson.annotations.SerializedName
import io.github.wulkanowy.sdk.mobile.ApiRequest
import java.util.UUID

data class CertificateRequest(

    @SerializedName("PIN")
    val pin: String,

    @SerializedName("TokenKey")
    val tokenKey: String,

    @SerializedName("AppVersion")
    val appVersion: String = MOBILE_APP_VERSION,

    @SerializedName("DeviceId")
    val deviceId: String = UUID.randomUUID().toString(),

    @SerializedName("DeviceName")
    val deviceName: String,

    @SerializedName("DeviceNameUser")
    val deviceNameUser: String = "",

    @SerializedName("DeviceDescription")
    val deviceDescription: String = "",

    @SerializedName("DeviceSystemType")
    val deviceSystemType: String = "Android",

    @SerializedName("DeviceSystemVersion")
    val deviceSystemVersion: String = "8.1.0"

) : ApiRequest()

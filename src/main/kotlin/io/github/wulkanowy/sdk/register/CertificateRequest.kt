package io.github.wulkanowy.sdk.register

import com.google.gson.annotations.SerializedName
import io.github.wulkanowy.sdk.APP_VERSION
import io.github.wulkanowy.sdk.base.BaseRequest
import io.github.wulkanowy.sdk.DEVICE_SYSTEM_TYPE
import io.github.wulkanowy.sdk.DEVICE_SYSTEM_VERSION
import java.util.*

data class CertificateRequest(

    @SerializedName("PIN")
    val pin: String,

    @SerializedName("TokenKey")
    val tokenKey: String,

    @SerializedName("AppVersion")
    val appVersion: String = APP_VERSION,

    @SerializedName("DeviceId")
    val deviceId: String = UUID.randomUUID().toString(),

    @SerializedName("DeviceName")
    val deviceName: String,

    @SerializedName("DeviceNameUser")
    val deviceNameUser: String = "",

    @SerializedName("DeviceDescription")
    val deviceDescription: String = "",

    @SerializedName("DeviceSystemType")
    val deviceSystemType: String = DEVICE_SYSTEM_TYPE,

    @SerializedName("DeviceSystemVersion")
    val deviceSystemVersion: String = DEVICE_SYSTEM_VERSION

) : BaseRequest()

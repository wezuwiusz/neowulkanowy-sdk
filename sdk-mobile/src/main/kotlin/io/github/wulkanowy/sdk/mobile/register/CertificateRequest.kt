package io.github.wulkanowy.sdk.mobile.register

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.github.wulkanowy.sdk.mobile.ApiRequest
import java.util.UUID

@JsonClass(generateAdapter = true)
data class CertificateRequest(

    @Json(name = "PIN")
    val pin: String,

    @Json(name = "TokenKey")
    val tokenKey: String,

    @Json(name = "AppVersion")
    val appVersion: String = MOBILE_APP_VERSION,

    @Json(name = "DeviceId")
    val deviceId: String = UUID.randomUUID().toString(),

    @Json(name = "DeviceName")
    val deviceName: String,

    @Json(name = "DeviceNameUser")
    val deviceNameUser: String = "",

    @Json(name = "DeviceDescription")
    val deviceDescription: String = "",

    @Json(name = "DeviceSystemType")
    val deviceSystemType: String = "Android",

    @Json(name = "DeviceSystemVersion")
    val deviceSystemVersion: String = "8.1.0",

    @Json(name = "FirebaseTokenKey")
    val firebaseToken: String

) : ApiRequest()

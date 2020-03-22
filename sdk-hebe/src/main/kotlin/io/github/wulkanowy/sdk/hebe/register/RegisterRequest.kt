package io.github.wulkanowy.sdk.hebe.register

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class RegisterRequest(

    @SerializedName("Certificate")
    val certificate: String,

    @SerializedName("CertificateThumbprint")
    val certificateThumbprint: String,

    @SerializedName("CertificateType")
    val certificateType: String = "X509",

    @SerializedName("DeviceModel")
    val deviceModel: String,

    @SerializedName("OS")
    val os: String = "Android",

    @SerializedName("PIN")
    val pin: String,

    @SerializedName("SecurityToken")
    val securityToken: String,

    @SerializedName("SelfIdentifier")
    val selfIdentifier: String = UUID.randomUUID().toString()
)

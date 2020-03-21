package io.github.wulkanowy.sdk.hebe

import com.google.gson.annotations.SerializedName

data class ApiRequest<T>(

    @SerializedName("API")
    val apiVersion: Int = 1,

    @SerializedName("AppName")
    val appName: String = "DzienniczekPlus 2.0",

    @SerializedName("AppVersion")
    val appVersion: String = "1.0",

    @SerializedName("CertificateId")
    val certificateId: String,

    @SerializedName("Envelope")
    val envelope: T,

    @SerializedName("FirebaseToken")
    val firebaseToken: String,

    @SerializedName("RequestId")
    val requestId: String,

    @SerializedName("Timestamp")
    val timestamp: Long,

    @SerializedName("TimestampFormatted")
    val timestampFormatted: String
)

package io.github.wulkanowy.sdk.hebe

import kotlinx.serialization.SerialName
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC
import java.time.format.DateTimeFormatter.ofPattern
import java.util.UUID

data class ApiRequest<T>(

    @SerialName("API")
    val apiVersion: Int = 1,

    @SerialName("AppName")
    val appName: String = "DzienniczekPlus 2.0",

    @SerialName("AppVersion")
    val appVersion: String = "1.0",

    @SerialName("CertificateId")
    val certificateId: String,

    @SerialName("Envelope")
    val envelope: T,

    @SerialName("FirebaseToken")
    val firebaseToken: String,

    @SerialName("RequestId")
    val requestId: String = UUID.randomUUID().toString(),

    @SerialName("Timestamp")
    val timestamp: Long = LocalDateTime.now().toEpochSecond(UTC),

    @SerialName("TimestampFormatted")
    val timestampFormatted: String = LocalDateTime.now().format(ofPattern("yyyy-MM-dd hh:mm:ss")),
)

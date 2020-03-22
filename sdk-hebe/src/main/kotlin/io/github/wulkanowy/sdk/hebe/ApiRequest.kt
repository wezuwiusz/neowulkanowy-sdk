package io.github.wulkanowy.sdk.hebe

import com.google.gson.annotations.SerializedName
import org.threeten.bp.LocalDateTime.now
import org.threeten.bp.ZoneOffset.UTC
import org.threeten.bp.format.DateTimeFormatter.ofPattern
import java.util.UUID

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
    val requestId: String = UUID.randomUUID().toString(),

    @SerializedName("Timestamp")
    val timestamp: Long = now().toEpochSecond(UTC),

    @SerializedName("TimestampFormatted")
    val timestampFormatted: String = now().format(ofPattern("yyyy-MM-dd hh:mm:ss"))
)

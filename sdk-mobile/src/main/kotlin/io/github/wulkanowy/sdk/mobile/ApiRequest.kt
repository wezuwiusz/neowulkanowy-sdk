package io.github.wulkanowy.sdk.mobile

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime.now
import java.time.ZoneOffset
import java.util.UUID

@Suppress("unused")
abstract class ApiRequest(

    @SerializedName("RemoteMobileTimeKey")
    val remoteMobileTimeKey: Long = now().toEpochSecond(ZoneOffset.UTC),

    @SerializedName("TimeKey")
    val timeKey: Long = now().toEpochSecond(ZoneOffset.UTC) - 1,

    @SerializedName("RequestId")
    val requestId: String = UUID.randomUUID().toString(),

    @SerializedName("RemoteMobileAppVersion")
    val remoteMobileAppVersion: String = MOBILE_APP_VERSION,

    @SerializedName("RemoteMobileAppName")
    val remoteMobileAppName: String = "VULCAN-Android-ModulUcznia"
) {
    companion object {
        const val MOBILE_APP_VERSION = "20.1.1.447"
    }
}

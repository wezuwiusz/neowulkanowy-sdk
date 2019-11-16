package io.github.wulkanowy.sdk.mobile

import com.google.gson.annotations.SerializedName
import java.time.Instant.now
import java.util.*

@Suppress("unused")
abstract class ApiRequest(

    @SerializedName("RemoteMobileTimeKey")
    val remoteMobileTimeKey: Long = now().epochSecond,

    @SerializedName("TimeKey")
    val timeKey: Long = now().epochSecond - 1,

    @SerializedName("RequestId")
    val requestId: String = UUID.randomUUID().toString(),

    @SerializedName("RemoteMobileAppVersion")
    val remoteMobileAppVersion: String = MOBILE_APP_VERSION,

    @SerializedName("RemoteMobileAppName")
    val remoteMobileAppName: String = "VULCAN-Android-ModulUcznia"
) {
    companion object {
        const val MOBILE_APP_VERSION = "18.10.1.435"
    }
}

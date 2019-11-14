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
    val remoteMobileAppVersion: String = "18.4.1.388",

    @SerializedName("RemoteMobileAppName")
    val remoteMobileAppName: String = "VULCAN-Android-ModulUcznia"
)

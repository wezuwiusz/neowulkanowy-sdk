package io.github.wulkanowy.sdk.base

import com.google.gson.annotations.SerializedName
import java.time.Instant
import java.util.*

abstract class ApiRequest(

    @SerializedName("RemoteMobileTimeKey")
    val remoteMobileTimeKey: Long = Instant.now().epochSecond,

    @SerializedName("TimeKey")
    val timeKey: Long = Instant.now().epochSecond - 1,

    @SerializedName("RequestId")
    val requestId: String = UUID.randomUUID().toString(),

    @SerializedName("RemoteMobileAppVersion")
    val remoteMobileAppVersion: String = "18.4.1.388",

    @SerializedName("RemoteMobileAppName")
    val remoteMobileAppName: String = "VULCAN-Android-ModulUcznia"
)

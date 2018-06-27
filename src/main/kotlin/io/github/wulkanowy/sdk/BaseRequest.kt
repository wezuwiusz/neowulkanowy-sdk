package io.github.wulkanowy.sdk

import com.google.gson.annotations.SerializedName
import java.time.Instant
import java.util.*

abstract class BaseRequest(

    @SerializedName("RemoteMobileTimeKey")
    val remoteMobileTimeKey: Long = Instant.now().epochSecond,

    @SerializedName("TimeKey")
    val timeKey: Long = Instant.now().epochSecond - 1,

    @SerializedName("RequestId")
    val requestId: String = UUID.randomUUID().toString(),

    @SerializedName("RemoteMobileAppVersion")
    val remoteMobileAppVersion: String = REMOTE_MOBILE_APP_VERSION,

    @SerializedName("RemoteMobileAppName")
    val remoteMobileAppName: String = REMOTE_MOBILE_APP_NAME
)

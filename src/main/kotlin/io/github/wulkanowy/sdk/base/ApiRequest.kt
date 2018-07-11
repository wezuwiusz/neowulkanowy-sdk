package io.github.wulkanowy.sdk.base

import com.google.gson.annotations.SerializedName
import io.github.wulkanowy.sdk.REMOTE_MOBILE_APP_NAME
import io.github.wulkanowy.sdk.REMOTE_MOBILE_APP_VERSION
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
    val remoteMobileAppVersion: String = REMOTE_MOBILE_APP_VERSION,

    @SerializedName("RemoteMobileAppName")
    val remoteMobileAppName: String = REMOTE_MOBILE_APP_NAME
)

package io.github.wulkanowy.sdk.mobile

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime.now
import java.time.ZoneOffset
import java.util.UUID

@Suppress("unused")
@JsonClass(generateAdapter = true)
open class ApiRequest(

    @Json(name = "RemoteMobileTimeKey")
    val remoteMobileTimeKey: Long = now().toEpochSecond(ZoneOffset.UTC),

    @Json(name = "TimeKey")
    val timeKey: Long = now().toEpochSecond(ZoneOffset.UTC) - 1,

    @Json(name = "RequestId")
    val requestId: String = UUID.randomUUID().toString(),

    @Json(name = "RemoteMobileAppVersion")
    val remoteMobileAppVersion: String = MOBILE_APP_VERSION,

    @Json(name = "RemoteMobileAppName")
    val remoteMobileAppName: String = "VULCAN-Android-ModulUcznia"
) {
    companion object {
        const val MOBILE_APP_VERSION = "20.1.1.447"
    }
}

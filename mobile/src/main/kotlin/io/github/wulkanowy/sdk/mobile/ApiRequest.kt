package io.github.wulkanowy.sdk.mobile

import com.google.gson.annotations.SerializedName
import org.threeten.bp.LocalDateTime.now
import org.threeten.bp.ZoneOffset
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
        const val MOBILE_APP_VERSION = "18.10.1.435"
    }
}

package io.github.wulkanowy.sdk.mobile

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiResponse<out T>(

    @Json(name = "Status")
    val status: String,

    @Json(name = "TimeKey")
    val timeKey: String,

    @Json(name = "TimeValue")
    val timeValue: String,

    @Json(name = "RequestId")
    val requestId: String,

    @Json(name = "DayOfWeek")
    val dayOfWeek: String,

    @Json(name = "AppVersion")
    val appVersion: String,

    @Json(name = "Data")
    val data: T?,
)

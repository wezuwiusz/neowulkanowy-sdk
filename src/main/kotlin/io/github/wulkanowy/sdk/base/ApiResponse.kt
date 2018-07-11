package io.github.wulkanowy.sdk.base

import com.google.gson.annotations.SerializedName

data class ApiResponse<out T>(

        @SerializedName("Status")
        val status: String,

        @SerializedName("TimeKey")
        val timeKey: String,

        @SerializedName("TimeValue")
        val timeValue: String,

        @SerializedName("RequestId")
        val requestId: String,

        @SerializedName("DayOfWeek")
        val dayOfWeek: String,

        @SerializedName("AppVersion")
        val appVersion: String,

        @SerializedName("Data")
        val data: T?
)

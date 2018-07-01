package io.github.wulkanowy.sdk.base

import com.google.gson.annotations.SerializedName

abstract class BaseResponse {

    @SerializedName("Status")
    lateinit var status: String

    @SerializedName("TimeKey")
    lateinit var timeKey: String

    @SerializedName("TimeValue")
    lateinit var timeValue: String

    @SerializedName("RequestId")
    lateinit var requestId: String

    @SerializedName("DayOfWeek")
    lateinit var dayOfWeek: String

    @SerializedName("AppVersion")
    lateinit var appVersion: String
}

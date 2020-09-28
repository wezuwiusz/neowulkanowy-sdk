package io.github.wulkanowy.sdk.scrapper.mobile

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UnregisterDeviceRequest(

    @Json(name = "id")
    val id: Int
)

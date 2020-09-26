package io.github.wulkanowy.sdk.scrapper.messages

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DeleteMessageRequest(

    @Json(name = "folder")
    val folder: Int,

    @Json(name = "messages")
    val messages: List<Int>
)

package io.github.wulkanowy.sdk.scrapper.messages

import com.google.gson.annotations.SerializedName

data class DeleteMessageRequest(

    @SerializedName("folder")
    val folder: Int,

    @SerializedName("messages")
    val messages: List<Int>
)

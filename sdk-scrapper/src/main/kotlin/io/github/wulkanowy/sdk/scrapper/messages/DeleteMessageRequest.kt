package io.github.wulkanowy.sdk.scrapper.messages

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeleteMessageRequest(

    @SerialName("folder")
    val folder: Int,

    @SerialName("messages")
    val messages: List<Int>
)

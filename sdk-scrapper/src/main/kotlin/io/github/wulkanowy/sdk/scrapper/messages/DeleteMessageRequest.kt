package io.github.wulkanowy.sdk.scrapper.messages

import com.google.gson.annotations.SerializedName

data class DeleteMessageRequest(

    @SerializedName("IdWiadomosci")
    val messageId: Int
)

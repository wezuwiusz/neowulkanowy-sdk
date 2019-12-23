package io.github.wulkanowy.sdk.scrapper.messages

import com.google.gson.annotations.SerializedName

data class DeleteMessageRequest(

    @SerializedName("Id")
    val messageId: Int,

    @SerializedName("Folder")
    val folderId: Int

)

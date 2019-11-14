package io.github.wulkanowy.api.messages

import com.google.gson.annotations.SerializedName

data class DeleteMessageRequest(

    @SerializedName("Id")
    val messageId: Int,

    @SerializedName("Folder")
    val folderId: Int

)

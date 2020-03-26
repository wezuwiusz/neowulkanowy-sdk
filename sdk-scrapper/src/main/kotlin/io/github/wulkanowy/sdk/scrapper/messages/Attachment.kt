package io.github.wulkanowy.sdk.scrapper.messages

import com.google.gson.annotations.SerializedName

data class Attachment(

    @SerializedName("Url")
    val url: String,

    @SerializedName("IdOneDrive")
    val oneDriveId: String,

    @SerializedName("IdWiadomosc")
    val messageId: Int,

    @SerializedName("NazwaPliku")
    val filename: String,

    @SerializedName("Id")
    val id: Int
)

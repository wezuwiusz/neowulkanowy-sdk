package io.github.wulkanowy.sdk.scrapper.messages

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Attachment(

    @SerialName("Url")
    val url: String,

    @SerialName("IdOneDrive")
    val oneDriveId: String,

    @SerialName("IdWiadomosc")
    val messageId: Int,

    @SerialName("NazwaPliku")
    val filename: String,

    @SerialName("Id")
    val id: Int
)

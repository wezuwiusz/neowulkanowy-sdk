package io.github.wulkanowy.sdk.scrapper.homework

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomeworkAttachment(

    @SerialName("IdZadanieDomowe")
    val homeworkId: Int,

    @SerialName("HtmlTag")
    val html: String,

    @SerialName("Url")
    val url: String,

    @SerialName("NazwaPliku")
    val filename: String,

    @SerialName("IdOneDrive")
    val oneDriveId: String,

    @SerialName("Id")
    val id: Int
)

package io.github.wulkanowy.sdk.scrapper.homework

import com.google.gson.annotations.SerializedName

data class HomeworkAttachment(

    @SerializedName("IdZadanieDomowe")
    val homeworkId: Int,

    @SerializedName("HtmlTag")
    val html: String,

    @SerializedName("Url")
    val url: String,

    @SerializedName("NazwaPliku")
    val filename: String,

    @SerializedName("IdOneDrive")
    val oneDriveId: String,

    @SerializedName("Id")
    val id: Int
)

package io.github.wulkanowy.sdk.scrapper.homework

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HomeworkAttachment(

    @Json(name = "IdZadanieDomowe")
    val homeworkId: Int,

    @Json(name = "HtmlTag")
    val html: String,

    @Json(name = "Url")
    val url: String,

    @Json(name = "NazwaPliku")
    val filename: String,

    @Json(name = "IdOneDrive")
    val oneDriveId: String,

    @Json(name = "Id")
    val id: Int
)

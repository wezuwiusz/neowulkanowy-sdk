package io.github.wulkanowy.sdk.scrapper.messages

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Attachment(

    @Json(name = "Url")
    val url: String,

    @Json(name = "IdOneDrive")
    val oneDriveId: String,

    @Json(name = "IdWiadomosc")
    val messageId: Int,

    @Json(name = "NazwaPliku")
    val filename: String,

    @Json(name = "Id")
    val id: Int
)

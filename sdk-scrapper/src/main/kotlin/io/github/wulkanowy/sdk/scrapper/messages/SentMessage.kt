package io.github.wulkanowy.sdk.scrapper.messages

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SentMessage(

    @Json(name = "Adresaci")
    val recipients: List<Recipient>,

    @Json(name = "Temat")
    val subject: String,

    @Json(name = "Tresc")
    val content: String,

    @Json(name = "Nadawca")
    val sender: Sender,

    @Json(name = "WiadomoscPowitalna")
    val isWelcomeMessage: Boolean,

    @Json(name = "Id")
    val id: Int
)

package io.github.wulkanowy.sdk.scrapper.messages

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SendMessageRequest(

    @Json(name = "incomming")
    val incoming: Incoming

) {

    @JsonClass(generateAdapter = true)
    data class Incoming(

        @Json(name = "Adresaci")
        val recipients: List<Recipient>,

        @Json(name = "Id")
        val id: Int = 0,

        @Json(name = "Nadawca")
        val sender: Sender = Sender(),

        @Json(name = "Temat")
        val subject: String,

        @Json(name = "Tresc")
        val content: String
    )
}

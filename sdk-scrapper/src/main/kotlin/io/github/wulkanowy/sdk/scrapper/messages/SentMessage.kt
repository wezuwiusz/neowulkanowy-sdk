package io.github.wulkanowy.sdk.scrapper.messages

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SentMessage(

    @SerialName("Adresaci")
    val recipients: List<Recipient>,

    @SerialName("Temat")
    val subject: String,

    @SerialName("Tresc")
    val content: String,

    @SerialName("Nadawca")
    val sender: Sender,

    @SerialName("WiadomoscPowitalna")
    val isWelcomeMessage: Boolean,

    @SerialName("Id")
    val id: Int
)

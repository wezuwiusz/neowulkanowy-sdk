package io.github.wulkanowy.sdk.scrapper.messages

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SendMessageRequest(

    @SerialName("incoming")
    val incoming: Incoming,

    @SerialName("incomming")
    val incomming: Incoming // for compatibility sake

) {

    @Serializable
    data class Incoming(

        @SerialName("Adresaci")
        val recipients: List<Recipient>,

        @SerialName("Id")
        val id: Int = 0,

        @SerialName("Nadawca")
        val sender: Sender = Sender(),

        @SerialName("Temat")
        val subject: String,

        @SerialName("Tresc")
        val content: String
    )
}

package io.github.wulkanowy.api.messages

import com.google.gson.annotations.SerializedName

data class SendMessageRequest(

    @SerializedName("incomming")
    val incoming: Incoming

) {

    data class Incoming(

        @SerializedName("Adresaci")
        val recipients: List<Recipient>,

        @SerializedName("Id")
        val id: Int = 0,

        @SerializedName("Nadawca")
        val sender: Sender = Sender(),

        @SerializedName("Temat")
        val subject: String,

        @SerializedName("Tresc")
        val content: String

    )
}

package io.github.wulkanowy.api.messages

import com.google.gson.annotations.SerializedName

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

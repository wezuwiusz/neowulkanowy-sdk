package io.github.wulkanowy.api.messages

import com.google.gson.annotations.SerializedName

data class SentMessage (

    @SerializedName("Adresaci")
    val recipients : List<Recipient>,

    @SerializedName("Temat")
    val subject: String,

    @SerializedName("Tresc")
    val content: String,

    @SerializedName("Nadawca")
    val sender: Sender,

    @SerializedName("WiadomoscPowitalna")
    val isWelcomeMessage: Boolean,

    @SerializedName("Id")
    val id: Int

)

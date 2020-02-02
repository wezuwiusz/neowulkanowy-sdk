package io.github.wulkanowy.sdk.mobile.messages

import com.google.gson.annotations.SerializedName

data class SendMessageRequest(

    @SerializedName("NadawcaWiadomosci")
    val sender: String,

    @SerializedName("Tytul")
    val subject: String,

    @SerializedName("Tresc")
    val content: String,

    @SerializedName("Adresaci")
    val recipients: List<Recipient>,

    @SerializedName("LoginId")
    val loginId: Int,

    @SerializedName("IdUczen")
    val studentId: Int
)

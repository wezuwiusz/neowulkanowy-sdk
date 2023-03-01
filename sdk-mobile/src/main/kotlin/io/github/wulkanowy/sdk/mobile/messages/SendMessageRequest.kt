package io.github.wulkanowy.sdk.mobile.messages

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SendMessageRequest(

    @Json(name = "NadawcaWiadomosci")
    val sender: String,

    @Json(name = "Tytul")
    val subject: String,

    @Json(name = "Tresc")
    val content: String,

    @Json(name = "Adresaci")
    val recipients: List<Recipient>,

    @Json(name = "LoginId")
    val loginId: Int,

    @Json(name = "IdUczen")
    val studentId: Int,
)

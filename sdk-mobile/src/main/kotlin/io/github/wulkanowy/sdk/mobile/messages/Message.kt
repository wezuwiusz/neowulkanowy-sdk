package io.github.wulkanowy.sdk.mobile.messages

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Message(

    @Json(name = "WiadomoscId")
    val messageId: Int,

    @Json(name = "Nadawca")
    val senderName: String?,

    @Json(name = "NadawcaId")
    val senderId: Int,

    @Json(name = "Adresaci")
    val recipients: List<Recipient>?,

    @Json(name = "Tytul")
    val subject: String,

    @Json(name = "Tresc")
    val content: String,

    @Json(name = "DataWyslania")
    val sentDate: String,

    @Json(name = "DataWyslaniaUnixEpoch")
    val sentDateTime: Long,

    @Json(name = "GodzinaWyslania")
    val sentHour: String,

    @Json(name = "DataPrzeczytania")
    val readDate: String?,

    @Json(name = "DataPrzeczytaniaUnixEpoch")
    val readDateTime: Long?,

    @Json(name = "GodzinaPrzeczytania")
    val readHour: String?,

    @Json(name = "StatusWiadomosci")
    val status: String,

    @Json(name = "FolderWiadomosci")
    val folder: String,

    @Json(name = "Nieprzeczytane")
    val unread: String?,

    @Json(name = "Przeczytane")
    val read: String?
)

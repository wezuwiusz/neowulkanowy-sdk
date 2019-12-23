package io.github.wulkanowy.sdk.mobile.messages

import com.google.gson.annotations.SerializedName

data class Message(

    @SerializedName("WiadomoscId")
    val messageId: Int,

    @SerializedName("Nadawca")
    val senderName: String?,

    @SerializedName("NadawcaId")
    val senderId: Int,

    @SerializedName("Adresaci")
    val recipients: List<Recipient>?,

    @SerializedName("Tytul")
    val subject: String,

    @SerializedName("Tresc")
    val content: String,

    @SerializedName("DataWyslania")
    val sentDate: String,

    @SerializedName("DataWyslaniaUnixEpoch")
    val sentDateTime: Long,

    @SerializedName("GodzinaWyslania")
    val sentHour: String,

    @SerializedName("DataPrzeczytania")
    val readDate: String?,

    @SerializedName("DataPrzeczytaniaUnixEpoch")
    val readDateTime: Long?,

    @SerializedName("GodzinaPrzeczytania")
    val readHour: String?,

    @SerializedName("StatusWiadomosci")
    val status: String,

    @SerializedName("FolderWiadomosci")
    val folder: String,

    @SerializedName("Nieprzeczytane")
    val unread: String?,

    @SerializedName("Przeczytane")
    val read: String?
)

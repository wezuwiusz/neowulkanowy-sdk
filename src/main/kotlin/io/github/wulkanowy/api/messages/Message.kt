package io.github.wulkanowy.api.messages

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Message(

    @SerializedName("Id")
    val id: Int?,

    @SerializedName("IdWiadomosci")
    val messageId: Int?,

    @SerializedName("NadawcaNazwa")
    val sender: String?,

    @SerializedName("IdNadawca")
    val senderId: Int?,

    @SerializedName("Adresaci")
    val recipient: String?,

    @SerializedName("Temat")
    val subject: String,

    @SerializedName("Tresc")
    val content: String?,

    @SerializedName("Data")
    val date: Date?,

    @SerializedName("FolderWiadomosci")
    val folderId: Int = 0,

    @SerializedName("Nieprzeczytana")
    val unread: Boolean?,

    @SerializedName("Nieprzeczytane")
    val unreadBy: Int?,

    @SerializedName("Przeczytane")
    val readBy: Int?
) {
    var recipientId: Int = 0

    var removed: Boolean = false

    var recipients: List<Recipient> = emptyList()
}

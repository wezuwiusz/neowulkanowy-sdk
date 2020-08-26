package io.github.wulkanowy.sdk.scrapper.messages

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
    val readBy: Int?,

    @SerializedName("HasZalaczniki")
    val hasAttachments: Boolean = false,

    @SerializedName("Zalaczniki")
    val attachments: List<Attachment>? = emptyList() // nullable just to make sure it doesn't break anything
) {
    var removed: Boolean = false
}

package io.github.wulkanowy.sdk.scrapper.messages

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Message(

    @SerializedName("Id")
    val id: Int?,

    @SerializedName("Nieprzeczytana")
    val unread: Boolean?,

    @SerializedName("Nieprzeczytane")
    val unreadBy: Int?,

    @SerializedName("Przeczytane")
    val readBy: Int?,

    @SerializedName("Data")
    val date: Date?,

    @SerializedName("Tresc")
    val content: String?,

    @SerializedName("Temat")
    val subject: String,

    @SerializedName("Nadawca")
    val sender: Recipient?,

    @SerializedName("IdWiadomosci")
    val messageId: Int?,

    @SerializedName("HasZalaczniki")
    val hasAttachments: Boolean = false,

    @SerializedName("FolderWiadomosci")
    val folderId: Int = 0,

    @SerializedName("Adresaci")
    val recipients: List<Recipient>?,

    @SerializedName("Zalaczniki")
    val attachments: List<Attachment>? = emptyList() // nullable just to make sure it doesn't break anything
) {
    var removed: Boolean = false
}

package io.github.wulkanowy.sdk.scrapper.messages

import io.github.wulkanowy.sdk.scrapper.adapter.CustomDateAdapter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class Message(

    @SerialName("Id")
    val id: Int?,

    @SerialName("Nieprzeczytana")
    val unread: Boolean?,

    @SerialName("Nieprzeczytane")
    val unreadBy: Int?,

    @SerialName("Przeczytane")
    val readBy: Int?,

    @SerialName("Data")
    @Serializable(with = CustomDateAdapter::class)
    val date: LocalDateTime?,

    @SerialName("Tresc")
    val content: String?,

    @SerialName("Temat")
    val subject: String?,

    @SerialName("Nadawca")
    val sender: Recipient?,

    @SerialName("IdWiadomosci")
    val messageId: Int?,

    @SerialName("HasZalaczniki")
    val hasAttachments: Boolean = false,

    @SerialName("FolderWiadomosci")
    val folderId: Int = 0,

    @SerialName("Adresaci")
    val recipients: List<Recipient>?,

    @SerialName("Zalaczniki")
    val attachments: List<Attachment>? = emptyList() // nullable just to make sure it doesn't break anything
) {
    var removed: Boolean = false
}

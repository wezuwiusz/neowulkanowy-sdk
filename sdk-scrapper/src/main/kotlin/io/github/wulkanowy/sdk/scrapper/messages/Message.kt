package io.github.wulkanowy.sdk.scrapper.messages

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class Message(

    @Json(name = "Id")
    val id: Int?,

    @Json(name = "Nieprzeczytana")
    val unread: Boolean?,

    @Json(name = "Nieprzeczytane")
    val unreadBy: Int?,

    @Json(name = "Przeczytane")
    val readBy: Int?,

    @Json(name = "Data")
    val date: Date?,

    @Json(name = "Tresc")
    val content: String?,

    @Json(name = "Temat")
    val subject: String?,

    @Json(name = "Nadawca")
    val sender: Recipient?,

    @Json(name = "IdWiadomosci")
    val messageId: Int?,

    @Json(name = "HasZalaczniki")
    val hasAttachments: Boolean = false,

    @Json(name = "FolderWiadomosci")
    val folderId: Int = 0,

    @Json(name = "Adresaci")
    val recipients: List<Recipient>?,

    @Json(name = "Zalaczniki")
    val attachments: List<Attachment>? = emptyList() // nullable just to make sure it doesn't break anything
) {
    var removed: Boolean = false
}

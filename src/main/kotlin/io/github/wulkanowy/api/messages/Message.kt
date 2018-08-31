package io.github.wulkanowy.api.messages

import com.google.gson.annotations.SerializedName
import java.util.*

data class Message(

        @SerializedName("FolderWiadomosci")
        var folderId: Int = 0,

        @SerializedName("Nieprzeczytana")
        val unread: Boolean,

        @SerializedName("Data")
        val date: Date,

        @SerializedName("Tresc")
        val content: String?,

        @SerializedName("Temat")
        val subject: String,

        @SerializedName("Adresaci")
        val recipient: String,

        val recipientId: Int = 0,

        @SerializedName("Nieprzeczytane")
        val unreadBy: Int?,

        @SerializedName("Przeczytane")
        val readBy: Int?,

        @SerializedName("NadawcaNazwa")
        val sender: String?,

        @SerializedName("IdWiadomosci")
        val messageId: Int,

        @SerializedName("IdNadawca")
        val senderId: Int,

        @SerializedName("Id")
        val id: Int
)

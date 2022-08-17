package io.github.wulkanowy.sdk.scrapper.messages

import io.github.wulkanowy.sdk.scrapper.adapter.CustomDateAdapter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class MessageReplayDetails(

    @SerialName("apiGlobalKey")
    val apiGlobalKey: String,

    @SerialName("data")
    @Serializable(with = CustomDateAdapter::class)
    val date: LocalDateTime,

    @SerialName("uzytkownikSkrzynkaGlobalKey")
    val mailboxId: String,

    @SerialName("nadawcaSkrzynkaGlobalKey")
    val senderMailboxId: String,

    @SerialName("nadawcaSkrzynkaNazwa")
    val senderMailboxName: String,

    @SerialName("adresaci")
    val recipients: List<Recipient>,

    @SerialName("temat")
    val subject: String,

    @SerialName("tresc")
    val content: String,

    @SerialName("zalaczniki")
    val attachments: List<MessageAttachment>,

    @SerialName("id")
    val id: Int,
)

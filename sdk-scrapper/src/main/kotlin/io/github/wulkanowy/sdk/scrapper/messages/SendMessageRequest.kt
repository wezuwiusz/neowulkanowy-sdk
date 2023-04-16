package io.github.wulkanowy.sdk.scrapper.messages

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SendMessageRequest(

    @SerialName("globalKey")
    val globalKey: String,

    @SerialName("watekGlobalKey")
    val threadGlobalKey: String,

    @SerialName("nadawcaSkrzynkaGlobalKey")
    val senderMailboxGlobalKey: String,

    @SerialName("adresaciSkrzynkiGlobalKeys")
    val recipientsMailboxGlobalKeys: List<String>,

    @SerialName("tytul")
    val subject: String,

    @SerialName("tresc")
    val content: String,

    @SerialName("zalaczniki")
    val attachments: List<MessageAttachment>,
)

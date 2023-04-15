package io.github.wulkanowy.sdk.scrapper.messages

import io.github.wulkanowy.sdk.scrapper.adapter.CustomDateAdapter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class MessageDetails(

    @SerialName("apiGlobalKey")
    val apiGlobalKey: String,

    @SerialName("data")
    @Serializable(with = CustomDateAdapter::class)
    val date: LocalDateTime,

    @SerialName("nadawca")
    val sender: String,

    @SerialName("odbiorcy")
    val recipients: List<String>,

    @SerialName("temat")
    val subject: String,

    @SerialName("tresc")
    val content: String,

    @SerialName("odczytana")
    val isRead: Boolean,

    @SerialName("zalaczniki")
    val attachments: List<MessageAttachment>,

    @SerialName("id")
    val id: Int,
)

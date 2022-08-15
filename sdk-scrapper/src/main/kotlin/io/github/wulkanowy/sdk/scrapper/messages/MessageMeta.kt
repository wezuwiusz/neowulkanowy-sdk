package io.github.wulkanowy.sdk.scrapper.messages

import io.github.wulkanowy.sdk.scrapper.adapter.CustomDateAdapter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class MessageMeta(

    @SerialName("apiGlobalKey")
    val apiGlobalKey: String,

    @SerialName("korespondenci")
    val correspondents: String,

    @SerialName("temat")
    val subject: String,

    @SerialName("data")
    @Serializable(with = CustomDateAdapter::class)
    val date: LocalDateTime,

    @SerialName("skrzynka")
    val mailbox: String,

    @SerialName("hasZalaczniki")
    val isAttachments: Boolean,

    @SerialName("przeczytana")
    val isRead: Boolean,

    // @SerialName("nieprzeczytanePrzeczytanePrzez")

    @SerialName("wazna")
    val isMarked: Boolean,

    @SerialName("uzytkownikRola")
    val userRole: Int,

    @SerialName("id")
    val id: Int,
)

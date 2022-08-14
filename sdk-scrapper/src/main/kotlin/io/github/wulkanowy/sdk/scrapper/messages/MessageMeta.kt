package io.github.wulkanowy.sdk.scrapper.messages

import io.github.wulkanowy.sdk.scrapper.adapter.CustomDateAdapter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class MessageMeta(

    @SerialName("apiGlobalKey")
    val apiGlobalKey: String,

    @SerialName("data")
    @Serializable(with = CustomDateAdapter::class)
    val date: LocalDateTime,

    @SerialName("hasZalaczniki")
    val isAttachments: Boolean,

    @SerialName("id")
    val id: Int,

    @SerialName("korespondenci")
    val correspondents: String,

    // @SerialName("nieprzeczytanePrzeczytanePrzez")

    @SerialName("przeczytana")
    val isRead: Boolean,

    @SerialName("skrzynka")
    val mailbox: String,

    @SerialName("temat")
    val subject: String,

    @SerialName("uzytkownikRola")
    val userRole: Int,

    @SerialName("wazna")
    val isMarked: Boolean,
)

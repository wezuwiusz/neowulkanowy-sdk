package io.github.wulkanowy.sdk.scrapper.messages

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Mailbox(

    @SerialName("globalKey")
    val globalKey: String,

    @SerialName("nazwa")
    val name: String,

    @SerialName("typUzytkownika")
    val userType: Int,
)

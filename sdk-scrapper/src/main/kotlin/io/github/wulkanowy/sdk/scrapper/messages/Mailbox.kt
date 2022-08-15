package io.github.wulkanowy.sdk.scrapper.messages

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Mailbox(

    @SerialName("globalKey")
    val globalKey: String,

    @SerialName("nazwa")
    val name: String,

    @SerialName("typUzytkownika")
    val userType: Int,

    @Transient
    val type: RecipientType = RecipientType.UNKNOWN,

    @Transient
    val studentName: String = "",

    @Transient
    val schoolNameShort: String = "",
)

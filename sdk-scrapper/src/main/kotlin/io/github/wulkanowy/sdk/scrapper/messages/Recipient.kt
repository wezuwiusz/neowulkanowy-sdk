package io.github.wulkanowy.sdk.scrapper.messages

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Recipient(

    @SerialName("skrzynkaGlobalKey")
    val mailboxGlobalKey: String,

    @SerialName("nazwa")
    val name: String,

    @Transient
    val type: RecipientType = RecipientType.UNKNOWN,

    @Transient
    val studentName: String = "",

    @Transient
    val schoolNameShort: String = "",
)

enum class RecipientType(val letter: String) {
    STUDENT("U"),
    PARENT("R"),
    GUARDIAN("O"),
    EMPLOYEE("P"),

    UNKNOWN(""),
}

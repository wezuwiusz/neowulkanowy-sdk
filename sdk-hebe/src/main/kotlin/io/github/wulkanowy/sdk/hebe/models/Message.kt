package io.github.wulkanowy.sdk.hebe.models

import io.github.wulkanowy.sdk.hebe.CustomDateAdapter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Message(
    @SerialName("Id")
    val id: String,
    @SerialName("GlobalKey")
    val globalKey: String,
    @SerialName("ThreadKey")
    val threadKey: String,
    @SerialName("Subject")
    val subject: String,
    @SerialName("Content")
    val content: String,
    @SerialName("DateSent")
    val dateSent: Date,
    @SerialName("DateRead")
    val dateRead: Date?,
    @SerialName("Status")
    val status: Int,
    @SerialName("Sender")
    val sender: Sender,
    @SerialName("Receiver")
    val receiver: Array<Receiver>,
    @SerialName("Attachments")
    val attachments: Array<Attachment>,
    @SerialName("Importance")
    val importance: Int,
    @SerialName("Withdrawn")
    val withdrawn: Boolean,
) {
    @Serializable
    data class Date(
        @SerialName("Date")
        @Serializable(with = CustomDateAdapter::class)
        val date: LocalDate,
        @SerialName("DateDisplay")
        val dateDisplay: String,
        @SerialName("Time")
        val time: String,
        @SerialName("Timestamp")
        val timestamp: Long,
    )

    @Serializable
    data class Sender(
        @SerialName("GlobalKey")
        val globalKey: String,
        @SerialName("Name")
        val name: String,
    )

    @Serializable
    data class Receiver(
        @SerialName("GlobalKey")
        val globalKey: String,
        @SerialName("Name")
        val name: String,
        @SerialName("HasRead")
        val hasRead: Int? = null
    )

    @Serializable
    data class Attachment(
        @SerialName("Name")
        val name: String,
        @SerialName("Link")
        val link: String,
    )
}

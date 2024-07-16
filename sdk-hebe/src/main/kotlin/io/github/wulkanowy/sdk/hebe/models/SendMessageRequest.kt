package io.github.wulkanowy.sdk.hebe.models

import io.github.wulkanowy.sdk.hebe.CustomDateAdapter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class SendMessageRequest(
    @SerialName("Id")
    val id: String,
    @SerialName("GlobalKey")
    val globalKey: String,
    @SerialName("Partition")
    val partition: String,
    @SerialName("ThreadKey")
    val threadKey: String,
    @SerialName("Subject")
    val subject: String,
    @SerialName("Content")
    val content: String,
    @SerialName("Status")
    val status: Int,
    @SerialName("Owner")
    val owner: String,
    @SerialName("DateSent")
    val dateSent: DateSent,
    @SerialName("DateRead")
    val dateRead: DateRead?,
    @SerialName("Sender")
    val sender: Sender,
    @SerialName("Receiver")
    val receiver: List<Receiver>,
    @SerialName("Attachments")
    val attachments: List<Attachment>,
    @SerialName("Importance")
    val importance: Int,
    @SerialName("Withdrawn")
    val withdrawn: Boolean,
) {
    @Serializable
    data class DateSent(
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
    data class DateRead(
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
        @SerialName("Id")
        val id: String,
        @SerialName("Partition")
        val partition: String,
        @SerialName("Owner")
        val owner: String,
        @SerialName("GlobalKey")
        val globalKey: String,
        @SerialName("Name")
        val name: String,
        @SerialName("Group")
        val group: String,
        @SerialName("Initials")
        val initials: String,
        @SerialName("HasRead")
        val hasRead: Int,
        @SerialName("DisplayedClass")
        val displayedClass: DisplayedClass?,
    )

    @Serializable
    data class Receiver(
        @SerialName("Id")
        val id: String,
        @SerialName("Partition")
        val partition: String,
        @SerialName("Owner")
        val owner: String,
        @SerialName("GlobalKey")
        val globalKey: String,
        @SerialName("Name")
        val name: String,
        @SerialName("Group")
        val group: String,
        @SerialName("Initials")
        val initials: String,
        @SerialName("HasRead")
        val hasRead: Int,
        @SerialName("DisplayedClass")
        val displayedClass: DisplayedClass?,
    )

    @Serializable
    data class Attachment(
        @SerialName("Name")
        val name: String,
        @SerialName("Link")
        val link: String,
    )

    @Serializable
    data class DisplayedClass(
        @SerialName("DisplayedClass")
        val displayedClass: String?,
    )
}

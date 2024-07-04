package io.github.wulkanowy.sdk.hebe.models

import io.github.wulkanowy.sdk.hebe.CustomDateAdapter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Meeting(
    @SerialName("Id")
    val id: Int,
    @SerialName("Where")
    val where: String,
    @SerialName("Why")
    val why: String,
    @SerialName("Agenda")
    val agenda: String,
    @SerialName("AdditionalInfo")
    val additionalInfo: String?,
    @SerialName("Online")
    val online: String,
) {
    @Serializable
    data class When(
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
}

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
    val where: String? = null,
    @SerialName("Why")
    val why: String? = null,
    @SerialName("Agenda")
    val agenda: String? = null,
    @SerialName("AdditionalInfo")
    val additionalInfo: String? = null,
    @SerialName("Online")
    val online: String? = null,
    @SerialName("When")
    val `when`: When,
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

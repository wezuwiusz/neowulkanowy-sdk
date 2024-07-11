package io.github.wulkanowy.sdk.hebe.models

import io.github.wulkanowy.sdk.hebe.CustomDateAdapter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Vacation(
    @SerialName("Id")
    val id: Int,
    @SerialName("Name")
    val name: String,
    @SerialName("DateFrom")
    val dateFrom: DateFrom,
    @SerialName("DateTo")
    val dateTo: DateTo,
) {
    @Serializable
    data class DateFrom(
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
    data class DateTo(
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

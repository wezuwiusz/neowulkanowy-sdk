package io.github.wulkanowy.sdk.scrapper.attendance

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AttendanceRecordDay(

    @SerialName("Data")
    val date: String,

    @SerialName("Godziny")
    val hours: String,
)

package io.github.wulkanowy.sdk.scrapper.timetable

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimetableRequest(

    @SerialName("data")
    val date: String,
)

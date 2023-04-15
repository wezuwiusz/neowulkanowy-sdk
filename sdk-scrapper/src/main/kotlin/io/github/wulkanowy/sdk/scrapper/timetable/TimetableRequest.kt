package io.github.wulkanowy.sdk.scrapper.timetable

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TimetableRequest(

    @SerialName("data")
    val date: String,
)

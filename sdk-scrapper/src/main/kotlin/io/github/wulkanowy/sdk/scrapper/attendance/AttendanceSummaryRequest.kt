package io.github.wulkanowy.sdk.scrapper.attendance

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AttendanceSummaryRequest(
    @SerialName("idPrzedmiot")
    val id: Int?
)

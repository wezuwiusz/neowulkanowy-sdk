package io.github.wulkanowy.sdk.scrapper.attendance

import io.github.wulkanowy.sdk.scrapper.adapter.CustomDateAdapter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class AttendanceRequest(

    @SerialName("data")
    @Serializable(with = CustomDateAdapter::class)
    val date: LocalDateTime,

    @SerialName("idTypWpisuFrekwencji")
    val typeId: Int = -1,
)

@Serializable
data class AttendanceRecordsRequest(

    @SerialName("miesiac")
    val month: Int,
)

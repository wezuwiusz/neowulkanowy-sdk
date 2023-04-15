package io.github.wulkanowy.sdk.scrapper.homework

import io.github.wulkanowy.sdk.scrapper.adapter.CustomDateAdapter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class HomeworkRequest(

    @SerialName("date")
    @Serializable(with = CustomDateAdapter::class)
    val date: LocalDateTime,

    @SerialName("schoolYear")
    val schoolYear: Int,

    @SerialName("statusFilter")
    val statusFilter: Int,
)

package io.github.wulkanowy.sdk.scrapper.homework

import io.github.wulkanowy.sdk.scrapper.adapter.CustomDateAdapter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class HomeworkDay(

    @SerialName("Date")
    @Serializable(with = CustomDateAdapter::class)
    val date: LocalDateTime,

    @SerialName("Homework")
    val items: List<Homework>,

    @SerialName("Show")
    val show: Boolean,
)
